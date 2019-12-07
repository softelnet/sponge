# Dockerfile

FROM tomcat:9-jdk11

ENV CATALINA_HOME /usr/local/tomcat
ENV CATALINA_PROPERTIES $CATALINA_HOME/conf/catalina.properties

RUN apt-get -qq update \
    && apt-get -y -qq --no-install-recommends install python3 python3-dev python3-pip \
    && apt-get clean all \
    && rm -rf /var/lib/apt/lists/* \
    && pip3 install -U virtualenv \
    && mkdir -p /opt

RUN rm -rf $CATALINA_HOME/webapps/*
COPY target/sponge-demo-api.war $CATALINA_HOME/webapps/ROOT.war
COPY target/sponge-scripts.zip /opt/

RUN cd /opt \
    && unzip -q sponge-scripts.zip \
    && rm -f sponge-scripts.zip

ENV VIRTUAL_ENV /root/tensorflow
RUN virtualenv --system-site-packages -p python3 /root/tensorflow
ENV PATH="$VIRTUAL_ENV/bin:$PATH"

RUN pip3 install --upgrade -q pip \
    && pip3 install --upgrade -q -r /opt/sponge/digits/python/requirements.txt \
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

EXPOSE 8080 8081
