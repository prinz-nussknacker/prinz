FROM debian:buster-slim

RUN apt-get update && apt-get install -y wget curl git gnupg apt-utils gcc g++ make software-properties-common build-essential && apt-get update

RUN mkdir -p /opt
RUN cd /opt && git clone https://github.com/TouK/nussknacker.git

RUN wget -O- https://apt.corretto.aws/corretto.key | apt-key add -
RUN add-apt-repository 'deb https://apt.corretto.aws stable main'
RUN mkdir -p /usr/share/man/man1
RUN apt-get update && apt-get install -y java-1.8.0-amazon-corretto-jdk

RUN apt-get remove nodejs npm
RUN wget --quiet -O - https://deb.nodesource.com/gpgkey/nodesource.gpg.key | apt-key add - && \
    VERSION=node_15.x && \
    DISTRO="$(lsb_release -s -c)" && \
    echo "deb https://deb.nodesource.com/$VERSION $DISTRO main" | tee /etc/apt/sources.list.d/nodesource.list && \
    echo "deb-src https://deb.nodesource.com/$VERSION $DISTRO main" | tee -a /etc/apt/sources.list.d/nodesource.list
RUN apt-get update && apt-get install -y nodejs webpack
RUN wget https://www.npmjs.com/install.sh -O install.sh && \
    chmod +x install.sh && \
    ./install.sh && \
    rm -f install.sh
RUN cd /opt/nussknacker/ui/client && \
    npm install

COPY opt/ /opt/
RUN chmod +x /opt/nussknacker/run.sh

RUN cd /opt/nussknacker/ui && \
    ./buildServer.sh

CMD ["/opt/nussknacker/run.sh"]
