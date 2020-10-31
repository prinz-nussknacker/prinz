FROM conda/miniconda3:latest

ENV MLFLOW_HOME /opt/mlflow
ENV MLFLOW_VERSION 1.8.0
ENV SERVER_PORT 5000
ENV SERVER_HOST 0.0.0.0
ENV ARTIFACT_STORE ${MLFLOW_HOME}/artifactStore

RUN conda update conda && \
    conda install -c anaconda jupyter=1.0.0 && \
    conda install -c conda-forge mlflow=1.8.0 && \
    conda install -c anaconda scikit-learn=0.22.1 && \
    conda install -c anaconda psycopg2=2.8.5 && \
    conda install -c anaconda boto3=1.14.12 && \
    mkdir -p ${MLFLOW_HOME}/scripts && \
    mkdir -p ${ARTIFACT_STORE}

COPY run.sh ${MLFLOW_HOME}/scripts/run.sh
RUN chmod +x ${MLFLOW_HOME}/scripts/run.sh

EXPOSE ${SERVER_PORT}/tcp

VOLUME ["${MLFLOW_HOME}/scripts/", "${ARTIFACT_STORE}"]

WORKDIR ${MLFLOW_HOME}

CMD ["./scripts/run.sh"]
