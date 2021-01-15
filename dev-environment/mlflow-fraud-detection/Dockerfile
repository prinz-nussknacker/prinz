FROM continuumio/miniconda3:4.9.2
RUN apt-get update -qq

ENV MLFLOW_HOME=/opt/mlflow \
    MLFLOW_WORK_DIR=/home/mlflow \
    MLFLOW_SERVER_HOST=0.0.0.0 \
    MLFLOW_SERVER_PORT=5000

WORKDIR ${MLFLOW_WORK_DIR}/models/fraud-detection
COPY models/fraud-detection/ ./
RUN conda install --yes -q -c anaconda --file=anaconda-deps.txt && \
    conda install --yes -q -c conda-forge --file=conda-forge-deps.txt

WORKDIR ${MLFLOW_HOME}
COPY scripts/ scripts/
CMD scripts/run.sh
