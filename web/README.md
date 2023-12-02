# Web app

# Get stared

## Run locally

1. start the backend + database: `docker-compose run -d -p 8080:8080 -e SPRING_PROFILES_ACTIVE=dev app`
2. start the web app: `yarn dev`

# API specification / code generation

API-first approach is used to generate the API code from the OpenAPI specification.

The specification is located in the `resources/specs` folder and the code is generated using
the [openapi-generator](https://github.com/OpenAPITools/openapi-generator)
which already has a [Gradle plugin](https://github.com/OpenAPITools/openapi-generator/tree/master/modules/openapi-generator-gradle-plugin).

Run `./gradlew openApiGenerate` to generate the code. The generated code is located in the `build/generated` folder.


# References

* [generate api from swagger files](https://medium.com/glovo-engineering/using-contract-first-to-build-an-http-application-with-openapi-and-gradle-53b42c2c2094)