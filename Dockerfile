# Multi-stage build for Spring Boot application
FROM maven:3.8.6-openjdk-11-slim AS build

# Set working directory
WORKDIR /app

# Copy pom.xml first for better layer caching
COPY pom.xml .

# Copy minimal Maven settings to avoid custom repository issues
COPY minimal-settings.xml .

# Download dependencies (this layer will be cached if pom.xml doesn't change)
RUN mvn dependency:go-offline -B -s minimal-settings.xml

# Copy source code
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests -s minimal-settings.xml

# Runtime stage
# Using Eclipse Temurin (Adoptium) as replacement for deprecated openjdk images
FROM eclipse-temurin:11-jre

# Install necessary packages
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
    curl \
    && rm -rf /var/lib/apt/lists/*

# Create app user for security
RUN groupadd -r appuser && useradd -r -g appuser appuser

# Set working directory
WORKDIR /app

# Copy the built JAR file from build stage
# Spring Boot Maven plugin creates an executable JAR with all dependencies
COPY --from=build /app/target/VMAuthen-2.0.1.jar app.jar

# Copy Firebase credentials (if needed)
# Note: This file should exist in src/main/resources/ for Firebase integration
COPY --from=build /app/src/main/resources/firebase-credentials.json ./firebase-credentials.json

# Create directory for logs
RUN mkdir -p /app/logs && chown -R appuser:appuser /app

# Switch to non-root user
USER appuser

# Expose port
EXPOSE 9001

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:9001/actuator/health || exit 1

# Set JVM options for containerized environment
ENV JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
