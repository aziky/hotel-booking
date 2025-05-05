# Bước 1: Sử dụng image JDK với Maven để biên dịch ứng dụng
FROM maven:3.9.6-eclipse-temurin-21 AS builder


# Đặt thư mục làm việc trong container
WORKDIR /app

# Copy parent POM (quan trọng)
COPY ../pom.xml ../pom.xml

# Sao chép pom.xml vào container để cache dependencies
COPY pom.xml .

# Sao chép mã nguồn vào container
COPY src /app/src

# Biên dịch ứng dụng và tạo file JAR
RUN mvn clean package -DskipTests

# Bước 2: Sử dụng image JRE để chạy ứng dụng
FROM eclipse-temurin:21-jre

# Đặt thư mục làm việc trong container
WORKDIR /app

# Sao chép file JAR đã biên dịch từ image builder
COPY --from=builder /app/target/*.jar /app/app.jar

#EXPOSE ${SERVER_PORT}
EXPOSE 8084

# Chạy ứng dụng khi container khởi động
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
# Cổng mà ứng dụng sẽ lắng nghe

