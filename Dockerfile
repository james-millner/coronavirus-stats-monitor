FROM armv7/armhf-java8

RUN mkdir -p /app
WORKDIR /app

COPY build/libs/stats-monitor-0.0.1-SNAPSHOT.jar .
COPY start-up.sh .

ENTRYPOINT ["/app/start-up.sh"]

EXPOSE $PORT
