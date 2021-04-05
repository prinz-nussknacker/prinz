#!/bin/sh

fit_wine() { # alpha l1_ratio model_serve_port experiment_id
  sleep 5 &&
  echo "Starting new mlflow run..." &&
  cd $MLFLOW_HOME &&
  mlflow run --no-conda --experiment-id "$4" $MLFLOW_WORK_DIR/models/sklearn-elasticnet-wine -P alpha="$1" -P l1_ratio="$2" -P model_id="$4" 2>&1 | tee "run$3.out" &&
  echo "Mlflow run finished" &&
  RUN_ID="$(cat "run$3.out" | tail -n 1 | grep -oP "(?<=').*?(?=')")" &&
  rm -f "run$3.out" &&
  echo "Mlflow run id: $RUN_ID" &&
  echo "Serving trained model in Mlflow registry on port $3" &&
  cd $MLFLOW_HOME &&
  mlflow models serve --no-conda --model-uri "s3://mlflow/$4/$RUN_ID/artifacts/model" --host $MLFLOW_SERVER_HOST --port "$3"
}

fit_fraud_detection() { # serving_port experiment_id
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
  mlflow models serve --no-conda --model-uri "s3://mlflow/${experiment_id}/$RUN_ID/artifacts/model" --host $MLFLOW_SERVER_HOST --port "${serving_port}"
}

create_experiment_if_not_exists() { # experiment_id
  experiment_id=$1
  mlflow experiments list --view "all" | tail -n +3 | awk '{$1=$1;print}' | sed 's/\s.*$//' | grep -P "^${experiment_id}$"
  if [ $? -eq 1 ]; then
    echo "Creating new mlflow experiment with id $1"
    mlflow experiments create --experiment-name "Test experiment ${experiment_id}" --artifact-location "s3://mlflow/${experiment_id}"
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

create_experiment_if_not_exists 1 &&
create_experiment_if_not_exists 2 &&
create_experiment_if_not_exists 3 &&
fit_wine 0.42 0.5 $MODEL_1_PORT 1 &
fit_wine 0.84 0.5 $MODEL_2_PORT 2 &
fit_fraud_detection $MODEL_3_PORT 3
