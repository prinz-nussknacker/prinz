package pl.touk.nussknacker.prinz.model.repository
import pl.touk.nussknacker.prinz.model.{Model, ModelName}

class CompositeModelRepository private(repositories: List[ModelRepository]) extends ModelRepository {

  override def listModels: RepositoryResponse[List[Model]] =
    repositories.map(_.listModels).partition(_.isLeft) match {
      case (Nil, lists) => Right((for (Right(list) <- lists) yield list).flatten)
      case(exceptions,  _) => Left((for (Left(exc) <- exceptions) yield exc).head)
    }

  override def getModel(name: ModelName): RepositoryResponse[Model] = {
    val results = repositories.map(_.getModel(name))
      .partition(_.isRight)
    val (responses, errors) = results
    if (responses.nonEmpty) {
      responses.head
    }
    else {
      errors.head
    }
  }
}

object CompositeModelRepository {

  def apply(repositories: ModelRepository*): CompositeModelRepository = {
    if (repositories.size < 1) {
      throw new IllegalArgumentException("CompositeModelRepository needs at least one ModelRepository")
    }
    new CompositeModelRepository(repositories.toList)
  }
}
