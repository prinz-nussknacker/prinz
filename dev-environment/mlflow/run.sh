#!/bin/sh

echo "Starting Mlflow UI on port $SERVER_PORT"

cd $MLFLOW_HOME && \
mlflow server --backend-store-uri $BACKEND_STORE_URI --default-artifact-root $ARTIFACT_LOCATION --host $SERVER_HOST --port $SERVER_PORT &

sleep 5
echo "Starting new mlflow run..."

cd $MLFLOW_HOME && \
mlflow run --no-conda $MLFLOW_WORK_DIR/models/sklearn_elasticnet_wine -P alpha=0.42 > run.out 2>&1 && \
RUN_ID="$(cat run.out | tail -n 1 | grep -oP "(?<=').*?(?=')")" && \
rm -f run.out

echo "Mlflow run finished"
echo "Mlflow run id: $RUN_ID"

echo "Serving trained model in Mlflow registry on port $MODEL_PORT"

cd $MLFLOW_HOME && \
mlflow models serve --no-conda --model-uri s3://mlflow/0/$RUN_ID/artifacts/model --host $SERVER_HOST --port $MODEL_PORT
