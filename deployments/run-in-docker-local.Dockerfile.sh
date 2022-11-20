#!/bin/sh

docker build -t secure-chat-container .. --no-cache --progress=plain &&
docker-compose -f docker-compose.yml -f docker-compose.Dockerfile.yml up -d
