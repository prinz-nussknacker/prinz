FROM minio/minio:latest

ENV MINIO_HOME /opt/minio
ENV BUCKET_NAME mlflow
ENV STORAGE mlflow-storage

COPY run.sh ${MINIO_HOME}/run.sh
RUN chmod +x ${MINIO_HOME}/run.sh
WORKDIR ${MINIO_HOME}

ENTRYPOINT ["sh"]
CMD ["-c", "./run.sh"]