FROM openjdk:17

ARG JAR_FILE=src/api/build/libs/*.jar
COPY ${JAR_FILE} app.jar

ENV PORT=6000
