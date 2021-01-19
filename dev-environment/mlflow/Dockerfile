FROM continuumio/miniconda3:4.9.2
RUN apt-get update -qq

ENV MLFLOW_HOME=/opt/mlflow \
    MLFLOW_WORK_DIR=/home/mlflow \
    MLFLOW_SERVER_HOST=0.0.0.0 \
    MLFLOW_SERVER_PORT=5000 \
    MODEL_PORT=1234
EXPOSE ${MLFLOW_SERVER_PORT}/tcp

WORKDIR ${MLFLOW_WORK_DIR}/models/fraud-detection
COPY models/fraud-detection/deps ./deps
RUN conda install --yes -q -c anaconda --file=deps/anaconda-deps.txt && \
    conda install --yes -q -c conda-forge --file=deps/conda-forge-deps.txt
COPY models/fraud-detection/ ./

WORKDIR ${MLFLOW_HOME}
COPY scripts/ scripts/
CMD scripts/run.sh
