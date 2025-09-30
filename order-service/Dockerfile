FROM openjdk:17-jdk-slim
WORKDIR /app
#COPY target/order-service-0.0.1.jar app.jar
COPY target/*.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]
