# ---- Build Stage ----
FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -q
COPY src ./src
RUN mvn package -DskipTests -q

# ---- Runtime Stage ----
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
RUN addgroup --system sms && adduser --system --ingroup sms sms
COPY --from=builder /app/target/*.jar app.jar
RUN chown sms:sms app.jar
USER sms
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
