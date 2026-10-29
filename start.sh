#!/usr/bin/env bash
# Palaiž Spring Boot serveri (kā `npm run dev`). Apturēt: Ctrl+C
# Vajag Java 21+. macOS + Homebrew gadījumā Java tiek atrasta automātiski.
cd "$(dirname "$0")"
if [ -z "$JAVA_HOME" ] && [ -d /opt/homebrew/opt/openjdk@21 ]; then
  export JAVA_HOME=/opt/homebrew/opt/openjdk@21
fi
./mvnw spring-boot:run
