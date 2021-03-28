#!/bin/sh

docker-compose --env-file ../.env -f docker-compose.yaml -f docker-compose-h2o.yaml -f docker-compose-env.yaml stop
docker-compose --env-file ../.env -f docker-compose.yaml -f docker-compose-h2o.yaml -f docker-compose-env.yaml kill
docker-compose --env-file ../.env -f docker-compose.yaml -f docker-compose-h2o.yaml -f docker-compose-env.yaml rm -f -v
docker network prune -f
docker container prune -f
docker volume prune -f
