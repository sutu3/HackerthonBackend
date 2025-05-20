FROM maven:3.8.5-openjdk-21 AS builder
# (Hoặc phiên bản Java bạn dùng, ví dụ: eclipse-temurin:17-jdk-jammy)

WORKDIR /app

# Copy pom.xml trước để tận dụng caching
COPY pom.xml .
# Tùy chọn: Tải dependencies trước nếu muốn
# RUN mvn dependency:go-offline

# Copy source code
COPY src ./src

# Build ứng dụng, tạo file JAR trong /app/target/
RUN mvn package -DskipTests
# Bạn có thể thêm dòng này để kiểm tra output của build
# RUN ls -l /app/target/

# === Giai đoạn 2: Tạo image chạy ứng dụng ===
FROM eclipse-temurin:17-jre-jammy
# (Hoặc phiên bản JRE tương ứng)

WORKDIR /app

# Copy file JAR từ giai đoạn builder
# Đảm bảo tên file JAR của bạn sẽ khớp với *.jar
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080 # Hoặc port ứng dụng của bạn thực sự lắng nghe

# Đảm bảo application.properties/yml có server.port=${PORT:8080}
ENTRYPOINT ["java", "-jar", "app.jar"]