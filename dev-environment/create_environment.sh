#!/bin/bash

scalaV="2.12"

# Compile and package prinz if not packaged yet
cd .. &&
./sbtwrapper prinz_sample/assembly &&
cd - &&
mkdir -p nussknacker/opt/prinz/ &&
cp "../prinz_sample/target/scala-${scalaV}/prinz-sample-assembly-0.0.1-SNAPSHOT.jar" "./nussknacker/opt/prinz-sample/" &&

# Create external network to communication between nussknacker and mlflow proxy
docker network create dev-bridge-net

# Add -d flag to hide environment startup
docker-compose --env-file ../.env -f docker-compose.yaml -f docker-compose-env.yaml kill
docker-compose --env-file ../.env -f docker-compose.yaml -f docker-compose-env.yaml rm -f -v
docker-compose --env-file ../.env -f docker-compose.yaml -f docker-compose-env.yaml build
docker-compose --env-file ../.env \
               -f docker-compose.yaml \
               -f docker-compose-env.yaml \
               up --always-recreate-deps
