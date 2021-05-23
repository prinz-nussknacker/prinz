package pl.touk.nussknacker.prinz.mlflow.model.rest

import pl.touk.nussknacker.prinz.model.ModelRunException
import pl.touk.nussknacker.prinz.util.http.RestClientException

class RestModelRunException(cause: Throwable) extends ModelRunException(cause)
