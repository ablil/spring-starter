# Spring starter
This is a playground I used to test and discover new technologies or implement new concept I've learned at work or on my free time.

It's a fully functioning rest API with unit tests and it's build primarly with [spring framework](spring.io) and [kotlin](kotlinlang.org).

# Get started
### Run locally
1. Start the database `docker-compose up -d database`
2. Run the app `./gradlew bootRun`

**Important**: *make sure you fill the environment variable on **application-\*.yaml** files*

### Run with Docker / PROD

1. Update environment variables or config on *docker-compose.yml* if necessary
2. Run `docker-compose up -d`

# Development

### API specs / code generation

To make it easier and convenient to write code quickly, Kotlin code is generated from Open API specifications using [openapi generator](https://github.com/OpenAPITools/openapi-generator) plugin for Gradle.

Put your specs files in `resouces/specs` and run `./gradlew openApiGenerate` to generate Java interfaces annotated with @RestController and all necessary metadata.

The generated code is located in `build/generated` and can be imported into your code base.

The API code is generated from Open API specification files, which are located in `resources/specs`


### Docker

To build a docker image, run `./gradlew jibDockerBuild`, this will build a an image with the name `spring-starter/latest`.

Docker images are built and pushed to [ghcr](https://github.com/ablil/spring-starter/pkgs/container/spring-starter) with the tag `latest` for main branch and `SHORT_SHA` for pull requests.

*Under the hood the [jib](https://cloud.google.com/blog/products/application-development/introducing-jib-build-java-docker-images-better) plugin for gradlew is used, and it can be configured on **build.gradle.kts***

*Old packages are being cleaned each month through a github workflow `.github/workflows/cleanup.yaml`*

### Security

By default, most endpoints are protected with bearer token, except some technical endpoints which are accessible with Basic authentication.

To issue a bearer token, request it from Keycloak authorization server (run container locally)
```shell
curl --location 'http://localhost:8081/realms/{realm}/protocol/openid-connect/token' \
--header 'Content-Type: application/x-www-form-urlencoded' \
--data-urlencode 'client_id={client_id}' \
--data-urlencode 'username={username} \
--data-urlencode 'password={password}' \
--data-urlencode 'grant_type=password'
```

**Public endpoints** does NOT require any authentication credentials.

**Technical endpoints** requires basic authentication.

**API endpoints** and the rest requires bearer token (json web token)

### Email

Any email related features is ignored if the property `spring.mail.host` is missing (check `EmailClient` class).

For local development, a fake SMTP server is provided through a docker image and can be accessible from *http://localhost:8025*.

### Actuator

Spring actuator is accessible only for admin user, and it is secured with basic authentication, make sure to change the default password through configuration property
**app.audit.password**

`/actuator/health` is public and can be accessed without authentication.

# Known issues

* `kotlin-spring` generator from [OpenAPI generator](https://openapi-generator.tech/) does not yet support
`@Tag` annotation (check [#17211](https://github.com/OpenAPITools/openapi-generator/issues/17211)).