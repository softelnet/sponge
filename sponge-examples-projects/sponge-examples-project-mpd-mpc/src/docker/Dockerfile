# Dockerfile

ARG SPONGE_VERSION

FROM openksavi/sponge:$SPONGE_VERSION

RUN apt-get -qq update \
    && apt-get -y -qq install mpc \
    && apt-get clean all

COPY kb /opt/sponge/kb

WORKDIR /opt/sponge

ENTRYPOINT ["/opt/sponge/bin/sponge"]

CMD ["-c", "kb/mpd_mpc.xml"]

EXPOSE 1836 1837
