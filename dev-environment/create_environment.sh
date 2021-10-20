#!/bin/bash

scalaV="2.12"
prinzV="1.0.0-SNAPSHOT"

COMP_FILES=""
ENV_FILE="-f docker-compose-env.yaml"
COMPILE=true

if [ $# -eq 0 ]
then
    COMP_FILES="-f docker-compose-mlflow.yaml -f docker-compose-pmml.yaml -f docker-compose-h2o.yaml"
else
    for ARG in $@
    do
        if [ $ARG = "-e" ]
        then
            ENV_FILE=""
            COMPILE=false
        elif [ $ARG = "mlflow" ]
        then
            COMP_FILES+="-f docker-compose-mlflow.yaml "
        elif [ $ARG = "pmml" ]
        then
            COMP_FILES+="-f docker-compose-pmml.yaml "
        elif [ $ARG = "h2o" ]
        then
            COMP_FILES+="-f docker-compose-h2o.yaml "
        fi
    done
fi

if [ $COMPILE = true ]
then
    # Compile and package prinz if not packaged yet
    cd .. &&
    ./sbtwrapper prinz_sample/assembly &&
    cd - &&
    mkdir -p nussknacker/opt/prinz/ &&
    cp "../prinz_sample/target/scala-${scalaV}/prinz-sample-assembly-${prinzV}.jar" "./nussknacker/opt/prinz-sample/"
fi

# Create external network to communication between nussknacker and mlflow proxy
docker network create dev-bridge-net

# Add -d flag to hide environment startup
docker-compose --env-file ../.env ${COMP_FILES} ${ENV_FILE} kill
docker-compose --env-file ../.env ${COMP_FILES} ${ENV_FILE} rm -f -v
docker-compose --env-file ../.env ${COMP_FILES} ${ENV_FILE} build
docker-compose --env-file ../.env ${COMP_FILES} ${ENV_FILE} up --always-recreate-deps
