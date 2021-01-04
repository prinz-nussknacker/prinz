#!/bin/sh

docker-compose -f docker-compose.yaml -f docker-compose-env.yaml stop
docker-compose -f docker-compose.yaml -f docker-compose-env.yaml kill
docker-compose -f docker-compose.yaml -f docker-compose-env.yaml rm -f -v
docker network prune -f
docker container prune -f
docker volume prune -f
