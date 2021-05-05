FROM continuumio/miniconda3:4.9.2
ARG DEBIAN_FRONTEND=noninteractive

# Install OpenJDK
# See https://bugs.debian.org/cgi-bin/bugreport.cgi?bug=863199#23 for mkdir
RUN apt-get update -qq && \
    mkdir -p /usr/share/man/man1 && \
    apt-get install aria2 unzip openjdk-11-jre-headless -y -qq

ENV H2O_WORK_DIR=/home/h2o \
    H2O_SERVER_PORT=54321

EXPOSE ${H2O_SERVER_PORT}/tcp

WORKDIR ${H2O_WORK_DIR}
COPY deps/ deps/
RUN conda install --yes -q -c h2oai --file=deps/h2oai-deps.txt
RUN aria2c -x 8 http://h2o-release.s3.amazonaws.com/h2o/rel-zermelo/5/h2o-3.32.0.5.zip && \
    unzip h2o-3.32.0.5.zip

COPY exports/ exports/
COPY models/ models/
COPY scripts/ scripts/

RUN chmod +x scripts/train.sh
RUN chmod +x scripts/serve.sh
RUN chmod +x scripts/run.sh

CMD scripts/run.sh
