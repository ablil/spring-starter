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
3. start the web app: `cd web && yarn dev`

## Run with docker

Run `docker-compose up -d` and it will run all services behind an Nginx proxy simulating prod environment.

# Configuration

## Email

[ Mailtrap ]( https://mailtrap.io/ ) is configured as an email sandbox, set your credentials in **application.yml** or
you
your own email config
your email config

## Database

Postgres is used as a default database with default credentials, set your credentials through env variables or
check **application.yml**