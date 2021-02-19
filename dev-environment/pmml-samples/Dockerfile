FROM continuumio/miniconda3:4.9.2
RUN apt-get update -qq

WORKDIR /home/pmml/
COPY deps/ deps/
RUN conda install --yes -q -c anaconda --file=deps/anaconda-deps.txt
COPY models/ models/

COPY scripts/ scripts/
RUN chmod +x scripts/train.sh
CMD scripts/train.sh && sleep infinity
