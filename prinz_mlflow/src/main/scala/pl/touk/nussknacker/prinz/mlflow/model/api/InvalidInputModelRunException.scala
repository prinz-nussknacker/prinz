package pl.touk.nussknacker.prinz.mlflow.model.api

import pl.touk.nussknacker.prinz.model.ModelRunException

class InvalidInputModelRunException(message: String) extends ModelRunException(message)