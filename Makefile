PACKAGE = stat-monitor

TAG := $$(git log -1 --pretty=%h)
IMG := jmillnerdev/stats-monitor:latest

default: build

build: clean package build-docker-image

build-and-push: build push

clean:
	./gradlew clean

package:
	./gradlew build

build-docker-image:
	docker build -t jmillnerdev/stats-monitor .

push:
	docker push ${IMG}
