FROM continuumio/miniconda3:4.9.2
ARG DEBIAN_FRONTEND=noninteractive

# Install OpenJDK
# See https://bugs.debian.org/cgi-bin/bugreport.cgi?bug=863199#23 for mkdir
RUN apt-get update -qq && \
    mkdir -p /usr/share/man/man1 && \
    apt-get install openjdk-11-jre-headless -y -qq

# Set up models
WORKDIR /home/pmml/
COPY deps/ deps/
RUN pip -q install -r deps/requirements.txt && \
    conda install --yes -q -c anaconda --file=deps/anaconda-deps.txt
COPY exports/ exports/
COPY models/ models/

COPY scripts/ scripts/
RUN chmod +x scripts/train.sh
CMD scripts/train.sh && sleep infinity
