FROM openjdk:17

ARG JAR_FILE=src/api/build/libs/*.jar
COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java","-jar","/app.jar"]