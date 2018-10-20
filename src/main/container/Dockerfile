FROM openjdk:oracle as builder

RUN jlink \
    --add-modules java.base,java.logging \
    --verbose \
    --strip-debug \
    --compress 2 \
    --no-header-files \
    --no-man-pages \
    --output /opt/jre

FROM openjdk:oracle

COPY --from=builder /opt/jre /opt/jre

ENV LANG=C.UTF-8 \
    PATH=${PATH}:/opt/jre/bin

ADD modules /opt/app/modules

ARG JVM_OPTS
ENV JVM_OPTS=${JVM_OPTS}

CMD java ${JVM_OPTS} \
    --module-path /opt/app/modules \
    --module systems.cauldron.service.graphql

#TODO figure out jlink and modules make this actually work