# Prinz [![Run unit tests](https://github.com/prinz-nussknacker/prinz/actions/workflows/unit_tests.yaml/badge.svg?branch=master)](https://github.com/prinz-nussknacker/prinz/actions/workflows/unit_tests.yaml) [![Check code quality](https://github.com/prinz-nussknacker/prinz/actions/workflows/code_quality.yaml/badge.svg?branch=master)](https://github.com/prinz-nussknacker/prinz/actions/workflows/code_quality.yaml)

Prinz provides machine learning integrations for [Nussknacker](https://github.com/TouK/nussknacker) written in Scala 2.12.

A current version of library supports:
- [mlflow](https://github.com/mlflow/mlflow/) models registry integration
- [jpmml](https://github.com/jpmml) exported models files scoring
- [h2o](https://github.com/h2oai) models registry integration

Additionally, the library adds the ability for models proxying in order to add extra models
data sources in the [prinz_proxy](./prinz_proxy) module.

## Useful links

- [Project documentation](https://prinz-nussknacker.github.io/prinz/)
- [Project presentation](https://prinz-nussknacker.github.io/prinz/presentation.html)

## Releases
Prinz is released on GitHub packages registry so accessing the library
code needs using the GitHub authentication. Full sample of working Prinz
integration is available in [prinz_sample](./prinz_sample)

If you are already signed to GitHub in your project, just add any of these lines
to add Prinz dependencies to your `.sbt` project

```sbt
"pl.touk.nussknacker.prinz" %% "prinz" % "1.0.0-SNAPSHOT"
"pl.touk.nussknacker.prinz" %% "prinz-mlflow" % "1.0.0-SNAPSHOT"
"pl.touk.nussknacker.prinz" %% "prinz-pmml" % "1.0.0-SNAPSHOT"
"pl.touk.nussknacker.prinz" %% "prinz-h2o" % "1.0.0-SNAPSHOT"
"pl.touk.nussknacker.prinz" %% "prinz-proxy" % "1.0.0-SNAPSHOT"
```

## Authors

- Krzysztof Antoniak [@kantoniak](https://github.com/kantoniak)
- Michał Jadwiszczak [@Jadw1](https://github.com/Jadw1)
- Jan Kukowski [@xvk9](https://github.com/xvk9)
- Maciej Procyk [@avan1235](https://github.com/avan1235)
- Maciej Próchniak [@mproch](https://github.com/mproch) (project supervisor)

## License

The Prinz code (except [prinz_pmml](./prinz_pmml)) is shared under Apache License version 2.0.

The [prinz_pmml](./prinz_pmml) is shared under the GNU Affero General Public License v3.0.
