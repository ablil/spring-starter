FROM eclipse-temurin:17-jdk-alpine
COPY . .
EXPOSE 80
ENTRYPOINT ["./gradlew", "bootRun"]