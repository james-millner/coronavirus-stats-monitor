#!/usr/bin/env bash

cd /app

if [[ -n "$TRUSTSTORE_NAME" && -n "$SECRET_DIR" ]]; then
  JAVA_ARGS="$JAVA_ARGS -Djavax.net.ssl.trustStore=${SECRET_DIR}/${TRUSTSTORE_NAME}"
fi

if [ -n "$TRUSTSTORE_PASSWORD" ]; then
  JAVA_ARGS="$JAVA_ARGS -Djavax.net.ssl.trustStorePassword=${TRUSTSTORE_PASSWORD}"
fi

# turn off DNS caching for apps that need it turned off
if [[ -n "$DISABLE_DNS_CACHE" && "$DISABLE_DNS_CACHE" == "true" ]]; then
    JAVA_ARGS="$JAVA_ARGS -Dnetworkaddress.cache.ttl=0 -Dnetworkaddress.cache.negative.ttl=0"
fi

JAVA_ARGS = "$JAVA_ARGS -Dspring.profiles.active=docker"

# exec <cmd> replaces the current PID rather than spawning a child process meaning any signals
# sent by Kubernetes will be sent directly to the app rather than this process
exec java -jar $JAVA_ARGS stats-monitor-0.0.1-SNAPSHOT.jar
