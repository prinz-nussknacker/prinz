#!/bin/sh

# Compile and package prinz if not packaged yet
cd '..' &&
./sbtwrapper prinz/package prinz_sample/package &&
cd - &&
cp ../prinz/target/scala-2.12/prinz_2.12-0.0.1-SNAPSHOT.jar nussknacker/opt/prinz/ &&
cp ../prinz_sample/target/scala-2.12/prinz-sample_2.12-0.0.1-SNAPSHOT.jar nussknacker/opt/prinz-sample/ &&

# Add -d flag to hide environment startup
docker-compose up --build
