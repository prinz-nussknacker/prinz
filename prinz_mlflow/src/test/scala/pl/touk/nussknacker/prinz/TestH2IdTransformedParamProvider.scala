package pl.touk.nussknacker.prinz

import pl.touk.nussknacker.engine.api.typed.typing.Typed
import pl.touk.nussknacker.engine.util.SynchronousExecutionContext.ctx
import pl.touk.nussknacker.prinz.model.ModelInstance.ModelInputData
import pl.touk.nussknacker.prinz.model.{ModelSignature, SignatureField, SignatureName, SignatureType}
import pl.touk.nussknacker.prinz.model.proxy.tranformer.ModelInputTransformer

import java.sql.ResultSet
import java.sql.Types
import scala.concurrent.Future

class TestH2IdTransformedParamProvider(tableName: String) extends ModelInputTransformer {

  private var cachedColumnNames: Option[List[SignatureField]] = None

  override def transformInputData(originalParameters: ModelInputData): Future[ModelInputData] = {
    val ids = originalParameters.get(s"${tableName}_id")
      .map(optVector => optVector.map(v => v.asInstanceOf[Int]))
      .get
    val extraInputsNames = cachedColumnNames
      .getOrElse(initExtraColumnFields())
      .map(_.signatureName.name)
    Future(addExtraDataForIds(extraInputsNames, ids, originalParameters))
  }

  override def changeSignature(modelSignature: ModelSignature): ModelSignature = {
    val inputsToRemove = cachedColumnNames
      .getOrElse(initExtraColumnFields())
      .map(_.signatureName.name)
      .toSet
    val filteredInputs = modelSignature
      .signatureInputs
      .filter(input => !inputsToRemove.contains(input.signatureName.name))
    val extraInput = SignatureField(
      SignatureName(s"${tableName}_id"),
      SignatureType(Typed[Int])
    )
    ModelSignature(extraInput::filteredInputs, modelSignature.signatureOutputs)
  }

  private def addExtraDataForIds(extraInputsNames: List[String], ids: Iterable[Int], inputData: ModelInputData): ModelInputData =
    ids.foldLeft(inputData) { (acc, id) =>
      val rs = H2Database.executeNonEmptyQuery(s"select * from $tableName where ${tableName}_id = $id;")
      val extraValues = extraInputsNames.map(name => (name, rs.getObject(toColumnName(name))))
      acc addAll extraValues
    }

  private def initExtraColumnFields(): List[SignatureField] = {
    val rs = H2Database.executeNonEmptyQuery(s"select * from $tableName limit 1;")
    val extraColumns = getExtraColumnNames(rs)
    cachedColumnNames = Some(extraColumns)
    extraColumns
  }

  private def getExtraColumnNames(resultSet: ResultSet): List[SignatureField] = {
    val metadata = resultSet.getMetaData
    Iterator.from(1).take(metadata.getColumnCount)
      .map(idx => (metadata.getColumnName(idx), metadata.getColumnType(idx)))
      .filter(isNotIdData)
      .map(toSignatureField)
      .toList
  }

  private def toSignatureField(nameTypeId: (String, Int)): SignatureField = {
    val (name, typeId) = nameTypeId
    val typing = typeId match {
      case Types.VARCHAR => Typed[String]
      case Types.INTEGER => Typed[Int]
      case Types.DOUBLE => Typed[Double]
      case _: Any => throw new IllegalArgumentException(s"Unsupported type id: $typeId")
    }
    val trimmedName = name.substring(name.indexOf('_') + 1).toLowerCase()
    SignatureField(SignatureName(trimmedName), SignatureType(typing))
  }

  private def isNotIdData(data: (String, Int)): Boolean = !s"${tableName}_id".equalsIgnoreCase(data._1)

  private def toColumnName(signatureColumn: String): String = s"${tableName}_$signatureColumn".toUpperCase
}
