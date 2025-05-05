# Stage 1: Build all services
FROM maven:3.9.6-eclipse-temurin-21 AS builder

WORKDIR /app
COPY . .

RUN mvn clean package -DskipTests

# Stage 2: Runtime for api-gateway
FROM eclipse-temurin:21-jre as api-gateway
WORKDIR /usr/src/app
COPY --from=builder /app/api-gateway/target/api-gateway-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]

# Stage 3: Runtime for user-service
FROM eclipse-temurin:21-jre as user-service
WORKDIR /usr/src/app
COPY --from=builder /app/user-service/target/user-service-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]

# Stage 4: Runtime for recommendation-service
FROM eclipse-temurin:21-jre as recommendation-service
WORKDIR /usr/src/app
COPY --from=builder /app/recommendation-service/target/recommendation-service-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]

# Stage 5: Runtime for booking-service
FROM eclipse-temurin:21-jre as booking-service
WORKDIR /usr/src/app
COPY --from=builder /app/booking-service/target/booking-service-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]

# Stage 6: Runtime for payment-service
FROM eclipse-temurin:21-jre as payment-service
WORKDIR /usr/src/app
COPY --from=builder /app/payment-service/target/payment-service-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]

# Stage 7: Runtime for notification-service
FROM eclipse-temurin:21-jre as notification-service
WORKDIR /usr/src/app
COPY --from=builder /app/notification-service/target/notification-service-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
