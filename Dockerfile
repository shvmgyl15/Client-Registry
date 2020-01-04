FROM openjdk:8-jdk-alpine
VOLUME /tmp
COPY build/libs/* app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]