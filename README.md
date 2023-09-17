# Spring starter

[![Build-Test](https://github.com/ablil/spring-starter/actions/workflows/build-and-test.yml/badge.svg?branch=main)](https://github.com/ablil/spring-starter/actions/workflows/build-and-test.yml)

This is a boilerplate implemenation for a general purpose API.

# Implementation

The implementation includes:

* login with username/email and password
* registration with email confirmation
* reset and forget password
* Authorization with JWT (json web token)
* swagger / Open API specification

# Get started

## Run locally

To run locally, start a postgres database `docker-compose up -d database` then run the app `./gradlew bootRun`.

## Run with docker

Update the password credentials and add environment variables to `docker-compose.yml`, then run the containers (database
and app)
`docker-compose up -d`.

# Configuration

## Email

[ Mailtrap ]( https://mailtrap.io/ ) is configured as an email sandbox, set your credentials in **application.yml** or
you
your own email config
your email config

## Database

Postgres is used as a default database with default credentials, set your credentials through env variables or
check **application.yml**