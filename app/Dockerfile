FROM gradle:7.4.0-jdk17

WORKDIR /app

COPY gradle gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .
COPY gradlew .

RUN gradle installDist

CMD ./build/install/app/bin/app