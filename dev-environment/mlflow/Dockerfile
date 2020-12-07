FROM conda/miniconda3:latest

ENV MLFLOW_HOME /opt/mlflow
ENV MLFLOW_WORK_DIR /home/mlflow
ENV SERVER_PORT 5000
ENV SERVER_HOST 0.0.0.0

RUN mkdir -p ${MLFLOW_HOME}/scripts && \
    mkdir -p ${MLFLOW_WORK_DIR}

EXPOSE ${SERVER_PORT}/tcp

WORKDIR ${MLFLOW_HOME}

RUN apt-get update && apt-get install -y git && \
    conda update conda && \
    conda install -c anaconda jupyter=1.0.0 && \
    conda install -c anaconda scikit-learn=0.22.1 && \
    conda install -c anaconda psycopg2=2.8.5 && \
    conda install -c anaconda boto3=1.14.12 && \
    conda install -c conda-forge azure-core=1.9.0 && \
    conda install -c conda-forge azure-storage=0.36.0 && \
    conda install -c conda-forge azure-storage-blob=12.6.0 && \
    conda install -c conda-forge mlflow=1.10.0

COPY run.sh ${MLFLOW_HOME}/scripts/run.sh
RUN chmod +x ${MLFLOW_HOME}/scripts/run.sh

CMD ["./scripts/run.sh"]
