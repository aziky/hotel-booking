FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /app

# Copy toàn bộ project để Maven có thể resolve dependency giữa modules
COPY . /app

# Build only booking-service and its dependency: common
RUN mvn clean package -DskipTests -f /app/pom.xml -pl booking-service,common

FROM eclipse-temurin:21-jre
WORKDIR /usr/src/app
COPY --from=builder /app/booking-service/target/booking-service-0.0.1-SNAPSHOT.jar app.jar
COPY --from=builder /app/booking-service/.env .env
EXPOSE 8084
ENTRYPOINT ["java", "-jar", "app.jar"]
