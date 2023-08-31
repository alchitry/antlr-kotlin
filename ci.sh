#!/bin/bash

set -eu

echo "INFO: $(date) running clean"
./gradlew clean

echo "INFO: $(date) running publishToMavenLocal"
./gradlew publishToMavenLocal $extra

#  - cd antlr-kotlin-examples-js && ../gradlew --info clean check $extra && cd ..
