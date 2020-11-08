FROM conda/miniconda3:latest

ENV MLFLOW_HOME /opt/mlflow
ENV MLFLOW_WORK_DIR /home/mlflow
ENV MLFLOW_VERSION 1.8.0
ENV SERVER_PORT 5000
ENV SERVER_HOST 0.0.0.0

RUN apt-get update && apt-get install -y git

RUN conda update conda
RUN conda install -c anaconda jupyter=1.0.0
RUN conda install -c conda-forge mlflow=1.8.0
RUN conda install -c anaconda scikit-learn=0.22.1
RUN conda install -c anaconda psycopg2=2.8.5
RUN conda install -c anaconda boto3=1.14.12
RUN conda install -c conda-forge azure-core=1.8.2
RUN conda install -c conda-forge azure-storage=0.36.0
RUN conda install -c conda-forge azure-storage-blob=12.5.0

RUN mkdir -p ${MLFLOW_HOME}/scripts
RUN cd /home && git clone https://github.com/mlflow/mlflow

COPY run.sh ${MLFLOW_HOME}/scripts/run.sh
RUN chmod +x ${MLFLOW_HOME}/scripts/run.sh
EXPOSE ${SERVER_PORT}/tcp

WORKDIR ${MLFLOW_HOME}

CMD ["./scripts/run.sh"]
