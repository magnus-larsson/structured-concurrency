#!/usr/bin/env bash

set -e
set -x

# TODO is build & test included in bootBuildImage?
./gradlew bootBuildImage
docker compose up -d
cd e2e-tests
mvn test