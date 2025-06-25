# --- Build Stage ---
FROM maven:3.8.6-eclipse-temurin-17 AS builder
WORKDIR /build

# Copy core-service (để install vào local Maven repo)
COPY ../core-service ./core-service
RUN cd core-service && mvn clean install -DskipTests

# Copy chat-service source code và build
COPY ./chat-service ./chat-service
# Build chat-service
WORKDIR /build/chat-service
RUN mvn clean package -DskipTests

# --- Runtime Stage ---
FROM eclipse-temurin:17-jdk
WORKDIR /app

# Copy jar đã build từ stage trước
COPY --from=builder /build/chat-service/target/*.jar app.jar

# Chạy app
ENTRYPOINT ["java", "-jar", "app.jar"]
