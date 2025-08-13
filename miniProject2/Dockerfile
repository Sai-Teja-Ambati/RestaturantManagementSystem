# Use Maven with JDK 17 (Eclipse Temurin) as base image
FROM maven:3.9.11-eclipse-temurin-17 AS build

# Set working directory inside container
WORKDIR /app

# Copy Maven descriptor files first for dependency caching
COPY pom.xml .
COPY src ./src

# Build the Spring Boot application (skip tests for faster build, optional)
RUN mvn clean package -DskipTests

# ==============================================
# Use a slim runtime image for final container
FROM eclipse-temurin:17-jdk AS runtime

WORKDIR /app

# Copy the built jar from the build stage
COPY --from=build /app/target/*.jar app.jar

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]
