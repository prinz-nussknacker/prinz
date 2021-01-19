

## About
This Mlflow image is published on GitHub Container Registry with status page located [here](https://github.com/prinz-nussknacker/prinz/packages/537933).

## Usage of MLflow image
To use it on CI the `secrets.GITHUB_TOKEN` is used to authenticate to repository. To use it on your local machine you have to genrate Personal Access Token on [this website](https://github.com/settings/tokens) and save it to `~/token.txt` file. Then use
```
cat ~/token.txt | docker login https://docker.pkg.github.com -u YOUR_GITHUB_USERNAME --password-stdin
```
to login to Github Docker Registry and use the prepared version.

## Publish updated MLflow image
To publish updated version of this image you should build new version locally with command
```
docker build .
```
then find IMAGE_ID with command
```
docker images
```
and tag the specified image with command
```
docker tag IMAGE_ID docker.pkg.github.com/prinz-nussknacker/prinz/mlflow_server:TAG_VERSION
```
where TAG_VERSION should be changed as described [here](https://semver.org/).

Rebuild with tag using

```
docker build -t docker.pkg.github.com/prinz-nussknacker/prinz/mlflow-server:TAG_VERSION .
```

Finally, to publish tagged image use command
```
docker push docker.pkg.github.com/prinz-nussknacker/prinz/mlflow-server:TAG_VERSION
```
and check the result on status page.
