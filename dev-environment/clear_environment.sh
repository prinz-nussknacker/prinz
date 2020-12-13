#!/bin/sh

docker-compose -f docker-compose.yaml -f docker-compose-env.yaml kill &&
docker-compose -f docker-compose.yaml -f docker-compose-env.yaml rm -f -v &&
rm -rf ./data/minio/* &&
rm -rf ./data/postgres/*
