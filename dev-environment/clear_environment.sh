#!/bin/sh

MLFLOW_ENV="-f docker-compose-mlflow.yaml"
PMML_ENV="-f docker-compose-pmml.yaml"

COMP_FILES=""

if [ $# -eq 0 ]
then
    COMP_FILES="${MLFLOW_ENV} ${PMML_ENV}"
else
    for FRAMEWORK in $@
    do
        if [ $FRAMEWORK = "mlflow" ]
        then
            COMP_FILES+="${MLFLOW_ENV} "
        elif [ $FRAMEWORK = "pmml" ]
        then
            COMP_FILES+="${PMML_ENV} "
        fi
    done
fi

docker-compose --env-file ../.env ${COMP_FILES} -f docker-compose-env.yaml stop
docker-compose --env-file ../.env ${COMP_FILES} -f docker-compose-env.yaml kill
docker-compose --env-file ../.env ${COMP_FILES} -f docker-compose-env.yaml rm -f -v
docker network prune -f
docker container prune -f
docker volume prune -f
