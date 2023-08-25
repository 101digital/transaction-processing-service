#FROM adoptopenjdk/openjdk8-openj9:alpine-slim
FROM  containers.github.acsdigital.dev/adb/workflows/java:17.0.6_10
LABEL "base.image"="eclipse-temurin:17.0.6_10-jdk-focal"

ENV ENVIRONMENT default
ARG JAR_FILE=@project.artifactId@-@project.version@.jar
COPY target/*.jar app.jar

COPY newrelic/ ./newrelic/

EXPOSE 8080

CMD java -Djava.security.egd=file:/dev/./urandom -Dspring.profiles.active=$ENVIRONMENT -javaagent:./newrelic/newrelic.jar -jar ./app.jar
