FROM gradle:5.3.0-jdk-alpine AS builder
ENV APP_HOME=/usr/app/
WORKDIR $APP_HOME
COPY build.gradle settings.gradle $APP_HOME

COPY gradle $APP_HOME/gradle
COPY --chown=gradle:gradle . /home/gradle/src
USER root
RUN chown -R gradle /home/gradle/src

RUN gradle build || return 0
COPY . .
RUN gradle clean build


FROM openjdk:8-jre-alpine

ENV APPLICATION_USER ktor
RUN adduser -D -g '' $APPLICATION_USER

RUN mkdir /app
RUN chown -R $APPLICATION_USER /app

USER $APPLICATION_USER

ENV ITJ_ENV_MODE=dev
ENV MYSQL_DATABASE=itjuana
ENV MYSQL_USER=user
ENV MYSQL_PASSWORD=password
ENV MYSQL_ROOT_PASSWORD=password
ENV MYSQL_HOST=db
ENV GOOGLE_APPLICATION_CREDENTIALS=/

COPY --from=builder /usr/app/build/libs/example.jar /app/itjuana-backend.jar
WORKDIR /app

EXPOSE 8080

CMD ["java", "-server", "-XX:+UnlockExperimentalVMOptions", "-XX:+UseCGroupMemoryLimitForHeap", "-XX:InitialRAMFraction=2", "-XX:MinRAMFraction=2", "-XX:MaxRAMFraction=2", "-XX:+UseG1GC", "-XX:MaxGCPauseMillis=100", "-XX:+UseStringDeduplication", "-jar", "itjuana-backend.jar"]