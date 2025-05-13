FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
RUN mkdir -p /app/uploads
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.war"]
