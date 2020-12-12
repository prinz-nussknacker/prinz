#!/bin/bash

scalaV="2.12"
nussknackerV="0.3.0"

sampleDeps=(
    "../prinz_sample/target/pack/lib/nussknacker-flink-api_${scalaV}-${nussknackerV}.jar"
    "../prinz_sample/target/pack/lib/nussknacker-flink-util_${scalaV}-${nussknackerV}.jar"
    "../prinz_sample/target/pack/lib/nussknacker-model-flink-util_${scalaV}-${nussknackerV}.jar"
)

# Compile and package prinz if not packaged yet
cd .. &&
./sbtwrapper prinz/package prinz_sample/pack &&
cd - &&
mkdir -p nussknacker/opt/prinz/ &&
cp "../prinz/target/scala-${scalaV}/prinz_${scalaV}-0.0.1-SNAPSHOT.jar" "nussknacker/opt/prinz/" &&
cp "../prinz_sample/target/scala-${scalaV}/prinz-sample_${scalaV}-0.0.1-SNAPSHOT.jar" "nussknacker/opt/prinz-sample/" &&
cp "${sampleDeps[@]}" "nussknacker/opt/prinz-sample/" &&

# Add -d flag to hide environment startup
docker-compose -f docker-compose.yaml -f docker-compose-env.yaml up --build
