FROM eclipse-temurin:17-jdk-alpine
# TODO: run the spring app directly like in maven: ./mnvwn spring-boot:run -Dspring.profiles.active=test
COPY build/libs/springstarter-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]