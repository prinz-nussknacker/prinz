# Architecture overview

## Introduction

Prinz extensions are injected to a Nussknacker instance in the configuration phase.
Instance administrator should configure model repositories which discover and provide model instances.
Each model is interpreted and exposed to Nussknacker UI as an enricher.
When Nussknacker process is deployed, Prinz translates parameters for the external model, delegates scoring and brings results back to the process.

## Model discovery

Nussknacker configuration should use one or more instances of `ModelRepository`.
Repositories handle listing models available in configured storage.
Successful exchange with storage results in a list of model handles, which will be converted to enrichers.

Prinz comes with ready-to-use implementations using local filesystem or HTTP endpoint.
HTTP server is expected to expose list of models as JSON.
Developers can provide custom implementations of `ModelRepository`.

## Models

Prinz uses an intermediate internal model representiation for unified handling of integrations.
`Model` trait represents a single entry in the Nussknacker UI.
Model is identified by name and may contain version info.
For some integrations it is possible to fetch metadata without loading the whole model.

By calling `Model.toModelInstance` system instantiates the model.
Exact behavior is implementation dependent, but this call should:
1. prepare model for signature extraction,
1. prepare model for scoring.

Operation may require additional communication with the service.
At this point model would usually get loaded into memory if it is needed.

### Signatures

Each machine learning models has inputs and outputs.
Their names (or ordering) along with data types form a model signature.
Since range of supported types varies between different libraries, Prinz also abstracts signatures.

Each `ModelInstance` is instantiated with a signature provider, which will supply inputs and outputs of the model.
External data types are mapped to internal `SignatureType`s based on types supported by Nussknacker (see `TypingResult`).
Extracted features and outputs are available in the Nussknacker UI for process designers.

## Scoring

In a deployed Nussknacker process events move through the flow sequentially triggering computations at each step.
When computation reaches Prinz enricher, it triggers scoring for supplied inputs.
(At this point system does not support batching).
Scoring starts by calling `run` on a `PrinzEnricher`.
During the scoring phase Prinz:
1. converts inputs to external types based on the model signature,
1. triggers scoring and receives outputs,
1. translates results from external types to the ones used by Nussknacker.

Received outputs may be used for further processing.
