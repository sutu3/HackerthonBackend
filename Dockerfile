# Sử dụng một base image OpenJDK. Chọn phiên bản Java phù hợp với dự án của bạn.
# Ví dụ: openjdk:17-jdk-slim, openjdk:21-jdk-slim
FROM openjdk:21-jdk-slim

# Đặt biến môi trường cho tên file JAR (thay thế bằng tên JAR thực tế của bạn)
ARG JAR_FILE=target/*.jar
# Hoặc nếu dùng Gradle: ARG JAR_FILE=build/libs/*.jar

# Tạo thư mục làm việc
WORKDIR /app

# Copy file JAR đã được build vào container
# Điều này giả định bạn đã build JAR ở local hoặc CI/CD pipeline trước khi Docker build
# Hoặc, nếu bạn muốn Docker build làm luôn bước build Java:
# 1. Copy source code: COPY . .
# 2. Build bằng Maven/Gradle: RUN ./mvnw package -DskipTests (hoặc ./gradlew build -x test)
# Tuy nhiên, copy JAR đã build thường nhanh hơn và image nhỏ hơn.
COPY ${JAR_FILE} application.jar

# Expose port mà ứng dụng Spring Boot sẽ lắng nghe (Render sẽ map port này)
# Dù Spring Boot sẽ lắng nghe trên $PORT, việc EXPOSE giúp Docker biết port nào quan trọng.
EXPOSE 8080
# (Giá trị này không quá quan trọng khi Render tự động dùng biến $PORT, nhưng là good practice)

# Lệnh để chạy ứng dụng khi container khởi động
# Biến môi trường PORT sẽ được Render inject vào.
ENTRYPOINT ["java", "-jar", "/app/application.jar"]