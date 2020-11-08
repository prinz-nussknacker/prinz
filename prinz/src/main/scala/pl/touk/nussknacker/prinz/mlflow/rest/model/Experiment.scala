package pl.touk.nussknacker.prinz.mlflow.rest.model

case class Experiment(experiment_id: String, name: String, artifact_location: String, lifecycle_stage: String)

case class ListExperimentsResponse(experiments: List[Experiment])

case class CreateExperimentRequest(name: String, artifact_location: Option[String] = None)

case class CreateExperimentResponse(experiment_id: String)
