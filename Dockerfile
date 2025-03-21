FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copy pom.xml first for better caching
COPY pom.xml .

# Copy Maven wrapper files if you have them
COPY .mvn .mvn
COPY mvnw mvnw
COPY mvnw.cmd mvnw.cmd

# Download dependencies (with verbose output)
RUN mvn -B dependency:go-offline -e

# Copy source code
COPY src ./src

# Build with verbose output
RUN mvn -B package -DskipTests -e

FROM openjdk:21-slim
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
