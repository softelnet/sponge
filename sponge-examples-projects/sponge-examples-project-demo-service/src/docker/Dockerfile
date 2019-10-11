# Dockerfile

FROM tomcat:9-jre11

ENV CATALINA_HOME /usr/local/tomcat
ENV CATALINA_PROPERTIES $CATALINA_HOME/conf/catalina.properties

RUN apt-get -qq update \
    && apt-get -y -qq install procps unzip mc python3 python3-dev python3-pip \
    && apt-get clean all \
    && mkdir -p /opt

RUN rm -rf $CATALINA_HOME/webapps/*
COPY target/sponge-demo-api.war $CATALINA_HOME/webapps/ROOT.war
COPY target/sponge-scripts.zip /opt/

RUN cd /opt \
    && unzip -q sponge-scripts.zip \
    && rm -f sponge-scripts.zip \
    && pip3 install -q -r sponge/digits/python/requirements.txt \
    && mkdir -p /root/tensorflow/bin \
    && ln -s `which python3` /root/tensorflow/bin/python3 \
    && python3 -c "from tensorflow.keras import datasets; datasets.mnist.load_data()"

COPY password.txt /opt/sponge

RUN echo >> $CATALINA_PROPERTIES \
    && echo "# Sponge system properties" >> $CATALINA_PROPERTIES \
    && echo "sponge.home=/opt/sponge" >> $CATALINA_PROPERTIES \
    && echo "digits.home=/opt/sponge/digits" >> $CATALINA_PROPERTIES \
    && echo "password.file=/opt/sponge/password.txt" >> $CATALINA_PROPERTIES \
    && echo "sponge.grpc.port=8081" >> $CATALINA_PROPERTIES

WORKDIR /opt/sponge

CMD ["catalina.sh", "run"]

EXPOSE 8080
EXPOSE 8081
