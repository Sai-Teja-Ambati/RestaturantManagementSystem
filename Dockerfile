FROM maven:3.8-eclipse-temurin-17 AS build

# Set up working directory for building
WORKDIR /app

# Copy project files
COPY pom.xml .
COPY src ./src

# Build the application with explicit output
RUN echo "Starting Maven build..." && \
    mvn clean compile package -DskipTests -X && \
    echo "Build completed. Checking target directory:" && \
    ls -la /app/target/

# Verify the JAR file was created
RUN if [ ! -f "/app/target/restaurant-management-system-1.0.0.jar" ]; then \
        echo "ERROR: JAR file not found! Available files:"; \
        ls -la /app/target/; \
        exit 1; \
    else \
        echo "SUCCESS: JAR file created successfully"; \
        ls -la /app/target/restaurant-management-system-1.0.0.jar; \
    fi

# Runtime stage
FROM eclipse-temurin:17-jre

# Install PostgreSQL client and other utilities
RUN apt-get update && \
    apt-get install -y postgresql-client curl && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Copy the JAR file from build stage
COPY --from=build /app/target/restaurant-management-system-1.0.0.jar /app/app.jar

# Copy resources
COPY --from=build /app/src/main/resources/ /app/resources/

# Verify JAR file exists in runtime
RUN ls -la /app/ && \
    echo "Verifying JAR contents:" && \
    jar -tf /app/app.jar | head -20

# Expose the port
EXPOSE 8080

# Set proper terminal environment for interactive input
ENV TERM=xterm

# Set command to run with the -i flag to ensure System.in works properly
ENTRYPOINT ["java", "-Dfile.encoding=UTF-8", "-jar", "/app/app.jar"]
