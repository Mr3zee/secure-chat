#!/bin/sh

# This script runs Chat application in Docker on port 8080.

cd .. &&
rm server/application/build/jib-image.tar &&
./gradlew server:application:buildImage &&
echo "Docker image built." &&

echo "Running docker-compose. It may take a few minutes without any logs, be patient" &&
docker load < server/application/build/jib-image.tar &&

cd deployments &&
docker-compose -f docker-compose.yml -f docker-compose.local-app.yml up -d &&
echo "Chat is running on http://localhost:8080"
