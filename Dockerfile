# Multi-stage build for Spring Boot WAR deployment on fly.io
# Build stage
FROM maven:3.9-eclipse-temurin-17-alpine AS builder
WORKDIR /build

# Copy maven executable and pom.xml first for better caching
COPY mvnw mvnw.cmd ./
COPY pom.xml ./
COPY .mvn ./.mvn

# Maven package download using a separate layer for better caching
RUN chmod +x ./mvnw && \
    ./mvnw dependency:go-offline -B

# Copy source and build the application
COPY src ./src
RUN ./mvnw package -DskipTests

# Run stage - Using Tomcat to run the WAR
FROM tomcat:10-jre17-temurin
LABEL maintainer="FSK Courses Team"

# Install SQLite for better initialization and debugging
RUN apt-get update && apt-get install -y sqlite3 && rm -rf /var/lib/apt/lists/*

# Remove default Tomcat applications
RUN rm -rf /usr/local/tomcat/webapps/*

# Copy the WAR file from the build stage
COPY --from=builder /build/target/*.war /usr/local/tomcat/webapps/ROOT.war

# Create directories for SQLite database and file uploads
RUN mkdir -p /data /usr/local/tomcat/uploads && \
    chmod -R 777 /data /usr/local/tomcat/uploads

# Add startup script
COPY start.sh /usr/local/tomcat/
RUN chmod +x /usr/local/tomcat/start.sh

# Set environment variables
ENV SPRING_PROFILES_ACTIVE=prod
ENV JAVA_OPTS="-Xms128m -Xmx220m -Dspring.servlet.multipart.location=/usr/local/tomcat/uploads/ -Dserver.port=8080"

# Port for fly.io 
EXPOSE 8080

# Start using our script
CMD ["/usr/local/tomcat/start.sh"]
