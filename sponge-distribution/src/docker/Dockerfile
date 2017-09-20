# Dockerfile

FROM store/oracle/serverjre:8

ENV SPONGE_VERSION 1.0.4

RUN yum -y -q install unzip mc \
    && yum clean all \
    && cd /opt \
    && curl http://sponge.openksavi.org/download/sponge-${SPONGE_VERSION}-standalone.zip > tmp.zip && unzip -q tmp.zip && rm -f tmp.zip

WORKDIR /opt/sponge-${SPONGE_VERSION}/bin

CMD bash
