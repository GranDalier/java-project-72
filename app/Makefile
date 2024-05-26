.DEFAULT_GOAL := build-run

setup:
	./gradlew wrapper --gradle-version 8.7

clean:
	./gradlew clean

build-run:
	make build run

build:
	./gradlew clean build

run:
	./gradlew run

test:
	./gradlew test

report:
	./gradlew jacocoTestReport

lint:
	./gradlew checkstyleMain checkstyleTest

check-deps:
	./gradlew dependencyUpdates -Drevision=release

.PHONY: build