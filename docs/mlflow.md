# Prinz Mlflow integration

## Mlflow introduction

Mlflow is an opensource project for managing machine learning models lifecycle, including:

1. Experimentation and reproducibility - it allows to convert many machine learning models 
   format to standardized model format. User of Mlflow gets the ability to record and query
   experiments in standard formats which simplifies the reproduction process of model 
   deployment

2. Deployment - it allows to real-time serve the logged models as the webservices. 
   There are integrations with well known platforms for machine learning including 
   Microsoft Azure ML, Amazon SageMaker and Apache Spark UDF 

3. Central model registry - it can be used as a single repository with dedicated web 
   application for model versioning. It allows keeping models versions in single place a
   nd make them easily accessible for all developers in organization.

## Mlflow environment

To work with Mlflow developer need some instance of Mlflow server which would serve as 
the models' registry. Additionally, there are standalone webservices for realtime models
querying using web requests.

The whole environment in this repository is build from parts defined in docker-compose.yaml
file which includes the definition for

1. `mlflow-server` which works as the Mlflow model registry server and can be queried for
   already trained models and their train data specification. It has also the information 
   about the location of models signature location (which is a standalone S3 bucket)

2. `postgres-mlflow` is a database supporting the `mlflow-server` backend.

3. `aws-mlflow` acts as a S3 bucket server for holding models artifact storage

4. `proxy` keeps all the containers organized in a way that simulates the real-world
   example of models deployment.

To start the environment with the Nussknacker deployed with the Mlflow environment
use the [create_environment.sh](../dev-environment/create_environment.sh) script. 

## Model serving process

After the Mlflow environment is created there are sample models trained. They are just 
samples which aren't provided as real-world usage examples so shouldn't be used in production.

The models can be seen in web GUI of mlflow registry which is available as the web application
when Mlflow start. 

### Models scoring convention

Mlflow doesn't include the information of models' location for scoring. There are some conventions
that can be observed in the most popular platforms for using machine learning models like:

- [Databricks](https://docs.databricks.com/applications/mlflow/model-serving.html) which specifies
the invocation url by the pattern `<databricks-instance>/model/<registered-model-name>/<model-version>/invocations`
  
- Prinz internal convention which specifies the invocation url by `<localhost-instance>/model/<registered-model-name>/invocations`
  (so the version is missed as there are created randomly choson models versions for test purposes which
  don't differ by anything more than the version number)
