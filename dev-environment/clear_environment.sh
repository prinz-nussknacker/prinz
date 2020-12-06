#!/bin/sh

docker-compose down &&
rm -rf ./data/minio/* &&
rm -rf ./data/postgres/*
