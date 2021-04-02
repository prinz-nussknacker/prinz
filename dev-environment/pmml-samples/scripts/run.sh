#!/bin/sh

sh scripts/train.sh &&
python scripts/serve.py
