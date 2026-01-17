# Multi-stage build để tối ưu kích thước image

# Stage 1: Build stage
FROM eclipse-temurin:17-jdk-alpine AS builder

WORKDIR /app

# Install necessary tools
RUN apk add --no-cache bash

# Copy Maven wrapper
COPY .mvn .mvn
COPY mvnw .
RUN chmod +x ./mvnw

# Copy pom.xml first (for better caching)
COPY pom.xml .

# Download dependencies (cached layer)
RUN ./mvnw dependency:go-offline -B || true

# Copy source code
COPY src ./src

# Build application
RUN ./mvnw clean package -DskipTests -B

# Stage 2: Runtime stage
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Install curl and wget for healthcheck
RUN apk add --no-cache curl wget

# Copy JAR file from builder stage
COPY --from=builder /app/target/tara-academy-api-0.0.1-SNAPSHOT.jar app.jar

# Create logs directory
RUN mkdir -p /app/logs

# Expose port
EXPOSE 9999

# JVM options for production
ENV JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+UseG1GC -XX:+UseStringDeduplication"

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=90s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:9999/actuator/health || exit 1

# Run application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
