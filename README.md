# Spring starter

[![CI](https://github.com/ablil/spring-starter/actions/workflows/build-and-test.yml/badge.svg?branch=main)](https://github.com/ablil/spring-starter/actions/workflows/build-and-test.yml)

This is a boilerplate implementation for a general purpose API.

# Implementation

The implementation includes:

* login with username/email and password
* registration with email confirmation
* reset and forget password
* Authorization with JWT/cookie (json web token)
* swagger / Open API specification

# Get started

## Run locally
1. start the database: `docker-compose run -p 5432:5432 -d database`
2. start the spring app: `./gradlew bootRun`


# API specification / code generation

API-first approach is adopted in this project to generate the API code from the OpenAPI specification.

The specification is located in the `resources/specs` folder and the code is generated using
the [openapi-generator](https://github.com/OpenAPITools/openapi-generator)
which already has a [Gradle plugin](https://github.com/OpenAPITools/openapi-generator/tree/master/modules/openapi-generator-gradle-plugin).

Run `./gradlew openApiGenerate` to generate the code. The generated code is located in the `build/generated` folder.

# Docker

The [jib](https://github.com/GoogleContainerTools/jib/blob/master/jib-gradle-plugin/README.md) plugin is used to build
the docker image.

You just have to run `./gradlew jibDockerBuild` to build the image.


# Configuration

## Email

[ Mailtrap ]( https://mailtrap.io/ ) is configured as an email sandbox, set your credentials in **application.yml** or
you
your own email config
your email config

## Database

Postgres is used as a default database with default credentials, set your credentials through env variables or
check **application.yml**