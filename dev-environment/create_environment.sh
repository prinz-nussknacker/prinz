#!/bin/bash

scalaV="2.12"
nussknackerV="0.3.0"

# Compile and package prinz if not packaged yet
cd .. &&
./sbtwrapper prinz/package prinz_sample/assembly &&
cd - &&
mkdir -p nussknacker/opt/prinz/ &&
cp "../prinz/target/scala-${scalaV}/prinz_${scalaV}-0.0.1-SNAPSHOT.jar" "./nussknacker/opt/prinz/" &&
cp "../prinz_sample/target/scala-${scalaV}/prinz-sample-assembly-0.0.1-SNAPSHOT.jar" "./nussknacker/opt/prinz-sample/" &&

# Add -d flag to hide environment startup
docker-compose -f docker-compose.yaml -f docker-compose-env.yaml up --build
