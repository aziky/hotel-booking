FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /app

# Copy toàn bộ project để Maven có thể resolve dependency giữa modules
COPY . /app

# Compile common module first
RUN mvn clean compile -f /app/pom.xml -pl common

# Build only payment-service and its dependency: common
RUN mvn clean package -DskipTests -f /app/pom.xml -pl payment-service,common

FROM eclipse-temurin:21-jre
WORKDIR /usr/src/app
COPY --from=builder /app/payment-service/target/payment-service-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8084
ENTRYPOINT ["java", "-jar", "app.jar"]
