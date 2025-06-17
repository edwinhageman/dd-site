FROM eclipse-temurin:21-alpine AS builder
COPY . /app
WORKDIR /app
RUN ./mvnw clean package

FROM eclipse-temurin:21-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]