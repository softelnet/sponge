# Dockerfile

FROM openjdk:11

ENV SPONGE_VERSION 1.10.0

RUN apt-get -qq update \
    && apt-get -y -qq install procps unzip mc python3 python3-pip \
    && apt-get clean all \
    && pip3 install py4j \
    && mkdir -p /opt

COPY target/standalone/sponge-${SPONGE_VERSION}-standalone.zip /opt/

RUN cd /opt \
    && unzip -q sponge-${SPONGE_VERSION}-standalone.zip \
    && rm -f sponge-${SPONGE_VERSION}-standalone.zip \
    && ln -s -f sponge-${SPONGE_VERSION} sponge

WORKDIR /opt/sponge-${SPONGE_VERSION}/bin

CMD bash
