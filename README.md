The project is an implemenation of a RESTful API, and it serves as a playground for me test new concepts.
It's developed primarly with [Spring framework](spring.io) and [Kotlin](kotlinlang.org) and another bunch of technologies
  
# Get started 
1. Start a bunch of containers `docker-compose up -d`
2. Run the app `./gradlew bootRun`
3. Check app health `curl http://localhost:8080/actuator/health`
  
# Links
This is a non-exhaustive list of some useful links:
* Actuator:  http://localhost:8080/actuator
* Open API spec: http://localhost:8080/swagger-ui/index.html
* Keycloak admin console: http://localhost:8081/admin

# Development
## Keycloak
Keycloak container import a sample realm with couple of users and clients; check **local/realm.json** for details.

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
[OpenAPI Generator](https://github.com/OpenAPITools/openapi-generator/tree/master/modules/openapi-generator-gradle-plugin) Gradle plugin is used to generate code from Open API sepecifications with [Spring generator](https://openapi-generator.tech/docs/generators/spring), and they are defined in **src/main/resources/specs** .

Generate code
```shell
./gradlew openApiGenerate
```
## Docker
[Jib](https://cloud.google.com/blog/products/application-development/introducing-jib-build-java-docker-images-better) Gradle plugin is used to generate docker image, it's already configured as a  part of the build steps. And there is a Github action that pushes the image to [ghrc](https://cloud.google.com/blog/products/application-development/introducing-jib-build-java-docker-images-better)

Generate image
```shell
./gradlew jibDockerBuild
```
  
# Known issues  
  
* `kotlin-spring` generator from [OpenAPI generator](https://openapi-generator.tech/) does not yet support  
`@Tag` annotation (check [#17211](https://github.com/OpenAPITools/openapi-generator/issues/17211)).
