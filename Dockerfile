# ====== Build stage ======
FROM maven:3.9.3-eclipse-temurin-17 AS build
WORKDIR /workspace

# Copy pom and source code
COPY pom.xml .
COPY src ./src

# Package the Spring Boot app (skipping tests for speed)
RUN mvn -B -DskipTests clean package

# ====== Run stage ======
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app

# Copy the fat JAR from the build stage
COPY --from=build /workspace/target/*.jar app.jar

# Expose the default Spring Boot port
EXPOSE 8080

# Start the application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]