#!/bin/sh

echo "Starting Mlflow UI on port $SERVER_PORT"

cd $MLFLOW_HOME &&
mlflow server --backend-store-uri $BACKEND_STORE_URI --default-artifact-root $ARTIFACT_LOCATION --host $SERVER_HOST --port $SERVER_PORT &

create_mlflow_run() { # alpha l1_ratio model_serve_port experiment_id
  sleep 5 &&
  echo "Starting new mlflow run..." &&
  cd $MLFLOW_HOME &&
  mlflow run --no-conda --experiment-id "$4" $MLFLOW_WORK_DIR/models/sklearn_elasticnet_wine -P alpha="$1" -P l1_ratio="$2" -P model_id="$4" 2>&1 | tee "run$3.out" &&
  echo "Mlflow run finished" &&
  RUN_ID="$(cat "run$3.out" | tail -n 1 | grep -oP "(?<=').*?(?=')")" &&
  rm -f "run$3.out" &&
  echo "Mlflow run id: $RUN_ID" &&
  echo "Serving trained model in Mlflow registry on port $3" &&
  cd $MLFLOW_HOME &&
  mlflow models serve --no-conda --model-uri "s3://mlflow/$4/$RUN_ID/artifacts/model" --host $SERVER_HOST --port "$3"
}

create_experiment_if_not_exists() { # experiment_id
  mlflow experiments list --view "all" | tail -n +3 | awk '{$1=$1;print}' | sed 's/\s.*$//' | grep -P "^$1$"
  if [ $? -eq 1 ]; then
    echo "Creating new mlflow experiment with id $1"
    mlflow experiments create --experiment-name "Test experiment $1" --artifact-location "s3://mlflow/$1"
  fi
}

create_experiment_if_not_exists 1 &&
create_experiment_if_not_exists 2

create_mlflow_run 0.42 0.5 $MODEL_1_PORT 1 &
create_mlflow_run 0.84 0.5 $MODEL_2_PORT 2
