#!/usr/bin/env bash
# Palaiž visus testus (kā `npm test`)
cd "$(dirname "$0")"
if [ -z "$JAVA_HOME" ] && [ -d /opt/homebrew/opt/openjdk@21 ]; then
  export JAVA_HOME=/opt/homebrew/opt/openjdk@21
fi
./mvnw test
