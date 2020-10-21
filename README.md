# Coronavirus Stats Monitor

This was a simple tool I whipped up to run on my [K3s](https://k3s.io/) Raspberry Pi Cluster. 

It was purely an exercise to play with my local cluster and have a chance to explore Grafana / Prometheus for dashboards. I have a simple 2 node Raspberry Pi Cluster running an instance of the fantastic monitoring suite:

https://github.com/carlosedp/cluster-monitoring

The idea here was to create a simple Spring Boot app to run in the cluster which would be polled periodically by prometheus. The results would then be rendered with grafana:

![Alt text](example.png?raw=true "Example Dashboard") 

## Technologies

* JSoup
* Spring Boot
* Kotlin
* Gradle
* MariaDB

## Data Source

I used [Worldometer](http://www.worldometers.info/coronavirus/) as a datasource. The data was only used privately for display on a private Grafana Dashboard.

