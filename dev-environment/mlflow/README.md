

## About
This Mlflow image is published on GitHub Container Registry with status page located [here](https://github.com/prinz-nussknacker/prinz/packages/537933).

## Usage of MLflow image
To use it on CI the `secrets.GITHUB_TOKEN` is used to authenticate to repository. To use it on your local machine you have to genrate Personal Access Token on [this website](https://github.com/settings/tokens) and save it to `~/token.txt` file. Then use
```
cat ~/token.txt | docker login https://docker.pkg.github.com -u YOUR_GITHUB_USERNAME --password-stdin
```
to login to Github Docker Registry and use the prepared version.
