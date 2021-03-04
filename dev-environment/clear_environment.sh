#!/bin/sh

COMP_FILES=""

if [ $# -eq 0 ]
then
    COMP_FILES="-f docker-compose-mlflow.yaml -f docker-compose-pmml.yaml"
else
    for FRAMEWORK in $@
    do
        if [ $FRAMEWORK = "mlflow" ]
        then
            COMP_FILES+="-f docker-compose-mlflow.yaml "
        elif [ $FRAMEWORK = "pmml" ]
        then
            COMP_FILES+="-f docker-compose-pmml.yaml"
        fi
    done
fi

docker-compose --env-file ../.env ${COMP_FILES} -f docker-compose-env.yaml stop
docker-compose --env-file ../.env ${COMP_FILES} -f docker-compose-env.yaml kill
docker-compose --env-file ../.env ${COMP_FILES} -f docker-compose-env.yaml rm -f -v
docker network prune -f
docker container prune -f
docker volume prune -f
