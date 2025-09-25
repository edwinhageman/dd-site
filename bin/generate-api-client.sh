#!/usr/bin/env bash

cd $(dirname "$0")
curl http://localhost:8080/v3/api-docs > ../openapi.json
cd ..
mvn clean generate-sources