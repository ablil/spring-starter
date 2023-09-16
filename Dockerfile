FROM eclipse-temurin:17-jdk-alpine
COPY . .
ENTRYPOINT ["./gradlew", "bootRun"]