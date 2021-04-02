#!/bin/sh

python models/sklearn-elasticnet-wine/train.py 0.42 0.5 "exports/PMML-ElasticnetWineModel-1-v0-1.pmml" 1 &&
python models/sklearn-elasticnet-wine/train.py 0.84 0.5 "exports/PMML-ElasticnetWineModel-2-v0-2.pmml" 2 &&
python models/fraud-detection/train.py "exports/PMML-FraudDetection-3-v0-1.pmml" 3

echo "Training models finished"
