FROM openjdk:23-jdk-slim
ENV SPRING_PROFILES_ACTIVE=prod
WORKDIR /app
COPY target/task-management-system-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 5000
ENTRYPOINT ["java", "-jar", "/app/app.jar"]