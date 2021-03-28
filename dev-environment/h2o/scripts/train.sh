#!/bin/sh

# Wait for H2o server to start and initialize
sleep 30
# Run models training and exporting them to MOJO files
python models/glm-wine/train.py 0.42
python models/glm-wine/train.py 0.84
