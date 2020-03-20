FROM adoptopenjdk/openjdk13:armv7l-debian-jdk-13.0.2_8-slim

RUN mkdir -p /app
WORKDIR /app

COPY build/libs/stats-monitor-0.0.1-SNAPSHOT.jar .
COPY start-up.sh .

ENTRYPOINT ["/app/start-up.sh"]

EXPOSE $PORT
