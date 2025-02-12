FROM maven AS build

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn clean package -DskipTests -e

FROM openjdk:21-slim
WORKDIR /app

COPY --from=build /app/target/task-management-system-0.0.1-SNAPSHOT.jar /app/task-management-system-0.0.1-SNAPSHOT.jar

ENTRYPOINT ["java", "-jar", "/app/task-management-system-0.0.1-SNAPSHOT.jar"]
