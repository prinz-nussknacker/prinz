package pl.touk.nussknacker.prinz.util.http

trait RestRequestParams extends Product {

  def getParamsMap: Map[String, String] =
    getClass.getDeclaredFields
      .map(_.getName)
      .zip(productIterator.to)
      .toMap
      .mapValues(_.toString)
}

object RestRequestParams {

  def extractRequestParams(option: Option[RestRequestParams]): RestRequestParams =
    option.getOrElse(EmptyRestRequestParams)
}

object EmptyRestRequestParams extends RestRequestParams {

  override def getParamsMap: Map[String, String] = Map.empty

  override def productElement(n: Int): Any =
    throw new IndexOutOfBoundsException("EmptyRequestParams has no elements")

  override def productArity: Int = 0

  override def canEqual(that: Any): Boolean = false
}
