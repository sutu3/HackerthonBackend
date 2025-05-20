# === Giai đoạn 1: Build ứng dụng ===
# Sử dụng một base image Maven với JDK để build dự án.
# Chọn phiên bản JDK phù hợp với dự án của bạn (ví dụ: 17, 11)
FROM maven:3.8.5-openjdk-21 AS builder
# FROM maven:3.8.5-eclipse-temurin-17 AS builder # Một lựa chọn khác

# Đặt thư mục làm việc
WORKDIR /app

# Copy file pom.xml trước để tận dụng Docker layer caching
# Nếu pom.xml không thay đổi, các dependency sẽ không cần tải lại
COPY pom.xml .

# Tải các dependency (nếu pom.xml thay đổi)
# Bạn có thể dùng "mvn dependency:go-offline" nếu muốn tải hết trước khi copy source
RUN mvn dependency:resolve

# Copy toàn bộ source code của dự án vào container
COPY src ./src

# Build ứng dụng, bỏ qua tests để build nhanh hơn trên CI/CD
# File JAR sẽ được tạo trong thư mục /app/target/
RUN mvn package -DskipTests


# === Giai đoạn 2: Tạo image chạy ứng dụng ===
# Sử dụng một base image JRE nhỏ gọn hơn để chạy ứng dụng.
# Chọn phiên bản JRE tương ứng với JDK đã dùng để build.
FROM eclipse-temurin:17-jre-jammy
# FROM openjdk:17-jre-slim # Một lựa chọn khác

# Đặt thư mục làm việc
WORKDIR /app

# Copy file JAR đã được build từ giai đoạn "builder"
# Đảm bảo tên file JAR khớp hoặc sử dụng wildcard.
# Nếu bạn biết chính xác tên artifactId và version từ pom.xml, bạn có thể ghi rõ.
# Ví dụ: COPY --from=builder /app/target/hackerthon-0.0.1-SNAPSHOT.jar app.jar
COPY --from=builder /app/target/*.jar app.jar

# Expose port mà ứng dụng Spring Boot của bạn sẽ lắng nghe.
# Render sẽ tự động cung cấp biến môi trường PORT cho ứng dụng của bạn.
# Hãy đảm bảo application.properties/yml của bạn có server.port=${PORT:8080}
EXPOSE 8080

# Lệnh để chạy ứng dụng khi container khởi động.
# Render sẽ inject biến môi trường $PORT.
# Spring Boot sẽ tự động nhận biến server.port từ $PORT nếu được cấu hình đúng.
ENTRYPOINT ["java", "-jar", "app.jar"]