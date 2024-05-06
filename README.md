The project is an implementation of a RESTfull API, and it serves as a playground for me test new concepts.
It's developed primarily with [Spring framework](https://spring.io) and [Kotlin](https://kotlinlang.org) and another bunch of technologies
  
# Get started 
1. Start a bunch of containers `docker-compose up -d database keycloak`
2. Run the app `./gradlew bootRun`
3. Check app health `curl http://localhost:8080/actuator/health`
  
*`docker-compose up -d` will start all containers behind a nginx container exposed on port 80*

# Links
This is a non-exhaustive list of some useful links:
* Actuator:  http://localhost:8080/actuator
* Open API spec: http://localhost:8080/swagger-ui/index.html
* Keycloak admin console: http://localhost:8081/admin
* Keycloak account console: http://localhost:8081/realms/springstarter/account

# Development
## Keycloak
Keycloak container import a sample realm with a couple of users and clients; check **local/realm.json** for details.

Get access token:
```shell
curl 'http://0.0.0.0:8081/realms/springstarter/protocol/openid-connect/token' \
--header 'Content-Type: application/x-www-form-urlencoded' \
--data-urlencode 'client_id=springstarter' \
--data-urlencode 'username=bob' \
--data-urlencode 'password=supersecurepassword' \
--data-urlencode 'grant_type=password'
```
## API specification
[OpenAPI Generator](https://github.com/OpenAPITools/openapi-generator/tree/master/modules/openapi-generator-gradle-plugin) Gradle plugin is used to generate code from Open API specifications with [Spring generator](https://openapi-generator.tech/docs/generators/spring), and they are defined in **src/main/resources/specs** .

Generate code
```shell
./gradlew openApiGenerate
```
## Docker
[Jib](https://cloud.google.com/blog/products/application-development/introducing-jib-build-java-docker-images-better) Gradle plugin is used to generate docker image, it's already configured as a  part of the build steps. And there is a GitHub action that pushes the image to [ghrc](https://cloud.google.com/blog/products/application-development/introducing-jib-build-java-docker-images-better)

Generate image
```shell
./gradlew jibDockerBuild
```
  
# Known issues  
  
* `kotlin-spring` generator from [OpenAPI generator](https://openapi-generator.tech/) does not yet support  
`@Tag` annotation (check [#17211](https://github.com/OpenAPITools/openapi-generator/issues/17211)).
