FROM maven:3.9.16-eclipse-temurin-25 AS builder
WORKDIR /app
COPY settings.xml /root/.m2/settings.xml
COPY pom.xml .
COPY src ./src
RUN mvn -B -ntp -s /root/.m2/settings.xml clean package

FROM eclipse-temurin:25.0.4.1_1-jre
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
