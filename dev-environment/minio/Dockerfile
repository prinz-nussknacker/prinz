FROM minio/minio:RELEASE.2021-06-17T00-10-46Z-28-gac7697426

ENV MINIO_HOME /opt/minio
ENV BUCKET_NAME mlflow
ENV STORAGE mlflow-storage

WORKDIR ${MINIO_HOME}

COPY run.sh ${MINIO_HOME}/run.sh
RUN chmod +x ${MINIO_HOME}/run.sh

ENTRYPOINT ["sh"]
CMD ["-c", "./run.sh"]
