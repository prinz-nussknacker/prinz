#!/bin/sh

python models/fraud-detection/train.py &&
python models/sklearn-elasticnet-wine/train.py 0.42 0.5 &&
python models/sklearn-elasticnet-wine/train.py 0.84 0.5
