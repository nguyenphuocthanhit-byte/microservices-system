# eureka-server/Dockerfile
FROM openjdk:17-jdk-slim

# Thêm curl cho debug
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Copy JAR file
COPY target/*.jar app.jar

EXPOSE 8761

# Thêm Java options nếu cần
ENTRYPOINT ["java", "-Xms256m", "-Xmx512m", "-jar", "app.jar"]