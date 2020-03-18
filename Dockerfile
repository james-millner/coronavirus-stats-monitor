FROM adoptopenjdk/openjdk11-openj9:jdk-11.0.1.13-alpine-slim
RUN apk add --no-cache bash

RUN mkdir -p /app
WORKDIR /app

COPY build/libs/stats-monitor-0.0.1-SNAPSHOT.jar .
COPY start-up.sh .

ENTRYPOINT ["/app/start-up.sh"]

EXPOSE $PORT
