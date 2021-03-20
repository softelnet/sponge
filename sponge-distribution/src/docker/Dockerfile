# Dockerfile

FROM adoptopenjdk:11-jre-hotspot-bionic

ARG SPONGE_VERSION

RUN apt-get -qq update \
    && apt-get -y -qq --no-install-recommends install unzip python3 python3-pip git \
    && apt-get clean all \
    && rm -rf /var/lib/apt/lists/* \
    && pip3 install py4j \
    && mkdir -p /opt

COPY sponge-${SPONGE_VERSION}-standalone.zip /opt/

ENV PATH="/opt/sponge/bin:${PATH}"

RUN cd /opt \
    && unzip -q sponge-${SPONGE_VERSION}-standalone.zip \
    && rm -f sponge-${SPONGE_VERSION}-standalone.zip \
    && ln -s -f sponge-${SPONGE_VERSION} sponge \
    && chmod ug+x sponge/bin/sponge

WORKDIR /opt/sponge-${SPONGE_VERSION}

ENTRYPOINT ["sponge"]

CMD ["--help"]
