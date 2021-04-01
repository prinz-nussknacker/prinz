#!/bin/sh

python models/fraud-detection/train.py "exports/fraud-detection-v0-1.pmml" &&
python models/sklearn-elasticnet-wine/train.py 0.42 0.5 "exports/wine-042-05-v0-1.pmml" &&
python models/sklearn-elasticnet-wine/train.py 0.84 0.5 "exports/wine-084-05-v0-2.pmml"

echo "Training models finished"

