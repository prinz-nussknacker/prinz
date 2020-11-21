#!/bin/sh

mkdir -p /$STORAGE/$BUCKET_NAME
/usr/bin/minio server /$STORAGE
