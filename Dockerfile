FROM eclipse-temurin:23 AS build
WORKDIR /app

# Copy pom.xml first for better caching
COPY pom.xml .

# Copy Maven wrapper files if you have them
COPY .mvn .mvn
COPY mvnw mvnw
COPY mvnw.cmd mvnw.cmd

# Create the GraphQL schema directory and add a placeholder schema
RUN mkdir -p src/main/resources/graphql-client
RUN echo 'type Query { placeholder: String }' > src/main/resources/graphql-client/schema.graphqls

# Install Maven manually since Java 23 images might not have it
RUN apt-get update && apt-get install -y maven

# Copy source code
COPY src ./src

# Build with verbose output
RUN mvn package -DskipTests

FROM eclipse-temurin:23
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
