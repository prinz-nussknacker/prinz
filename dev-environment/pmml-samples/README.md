# PMML samples
This machine creates sample PMML files and serves them over HTTP.

HTTP port is exposed as `5100` by default.

Trained model definitions are regenerated at every start of the container
(by calling the [train.sh](./scripts/train.sh) script)