#!/bin/sh

create_mlflow_run() {
  serving_port=$1
  experiment_id=$2
  sleep 5 &&
  echo "Starting new mlflow run..." &&
  cd $MLFLOW_HOME &&
  mlflow run --no-conda --experiment-id "${experiment_id}" $MLFLOW_WORK_DIR/models/fraud-detection -P model_id="${experiment_id}" 2>&1 | tee "run${serving_port}.out" &&
  echo "Mlflow run finished" &&
  RUN_ID="$(cat "run${serving_port}.out" | tail -n 1 | grep -oP "(?<=').*?(?=')")" &&
  rm -f "run${serving_port}.out" &&
  echo "Mlflow run id: $RUN_ID" &&
  echo "Serving trained model in Mlflow registry on port ${serving_port}" &&
  cd $MLFLOW_HOME &&
  mlflow models serve --no-conda --model-uri "${MLFLOW_HOME}/mlruns/${experiment_id}/$RUN_ID/artifacts/model" --host $MLFLOW_SERVER_HOST --port "${serving_port}"
}

create_experiment_if_not_exists() { # experiment_id
  mlflow experiments list --view "all" | tail -n +3 | awk '{$1=$1;print}' | sed 's/\s.*$//' | grep -P "^$1$"
  if [ $? -eq 1 ]; then
    echo "Creating new mlflow experiment with id $1"
    mlflow experiments create --experiment-name "Test experiment $1" --artifact-location "${MLFLOW_HOME}/mlruns/$1"
  fi
}

echo "Starting Mlflow UI on port $MLFLOW_SERVER_PORT."
mlflow server \
  --backend-store-uri $BACKEND_STORE_URI \
  --default-artifact-root $ARTIFACT_LOCATION \
  --host $MLFLOW_SERVER_HOST \
  --port $MLFLOW_SERVER_PORT &

echo "Waiting for MLflow server to start up..."
sleep 10

create_experiment_if_not_exists 1
create_mlflow_run $MODEL_PORT 1
