# Dockerfile

FROM store/oracle/serverjre:8

ENV SPONGE_VERSION 1.10.0

RUN yum -y -q install unzip mc \
    && yum clean all \
    && curl "https://bootstrap.pypa.io/get-pip.py" -o "get-pip.py" \
    && python get-pip.py \
    && pip install py4j

ADD standalone /opt

RUN cd /opt \
    && unzip -q sponge-${SPONGE_VERSION}-standalone.zip \
    && rm -f sponge-${SPONGE_VERSION}-standalone.zip

WORKDIR /opt/sponge-${SPONGE_VERSION}/bin

CMD bash
