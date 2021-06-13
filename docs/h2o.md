# H2O integration

[H2O](https://www.h2o.ai/products/h2o/) is one of the leading machine learning platforms.
It features an open-source server with in-memory implementations of multiple popular algorithms.
Users can access the server through hosted visual notebooks or using multiple programming languages, including R and Python.
AutoML functionality helps to automatically select matching model types for provided datasets.
Created models can be deployed as
POJOs or MOJOs (_Model Object, Optimized_ - an alternative storage standard by H2O authors)
for scoring.
H2O provides both a server endpoint and a set of Java libraries for automated scoring based on deployment files.

## H2O in Prinz

Prinz uses deployment files in MOJO format.
Client shipped with Prinz allows for loading files from HTTP or local path.

Sample environment presents a minimal deployment:
- Models `.zip` files are stored on the server (see [`/dev-environment/h2o/exports/`]({{ book.sourcesRootUrl }}dev-environment/h2o/exports/), empty before training);
- Server exposes list of files as a website. [Minimal Python implementation]({{ book.sourcesRootUrl }}dev-environment/h2o/scripts/serve.py) lists files as a simple website at `localhost:5200`;
- Application [config]({{ book.sourcesRootUrl }}prinz-sample/src/main/scala/touk/nussknacker/prinz/sample/SampleConfigCreator.scala) includes H2O repository;
- Nussknacker [configuration file]({{ book.sourcesRootUrl }}dev-environment/nussknacker/opt/prinz-sample/prinz-application.conf) sets endpoint URL;
