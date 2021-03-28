## About
These images are published on GitHub Container Registry with status page located [here](https://github.com/prinz-nussknacker/prinz/packages/).


## Usage of GitHub docker images
To use it on CI the `secrets.GITHUB_TOKEN` is used to authenticate to repository. To use it on your local machine you have to genrate Personal Access Token on [this website](https://github.com/settings/tokens) and save it to `~/token.txt` file. Then use
```
cat ~/token.txt | docker login https://docker.pkg.github.com -u YOUR_GITHUB_USERNAME --password-stdin
```
to login to Github Docker Registry and use the prepared version.

## Publish updated image
To publish the updated version of any image you should build a new version locally with command
```
docker build .
```
then find IMAGE_ID with command
```
docker images
```
and tag the specified image giving it IMAGE_NAME and TAG_VERSION using command
```
docker tag IMAGE_ID docker.pkg.github.com/prinz-nussknacker/prinz/IMAGE_NAME:TAG_VERSION
```
where TAG_VERSION should be changed as described [here](https://semver.org/).

Rebuild with specified values using

```
docker build -t docker.pkg.github.com/prinz-nussknacker/prinz/IMAGE_NAME:TAG_VERSION .
```

Finally, to publish tagged image use command
```
docker push docker.pkg.github.com/prinz-nussknacker/prinz/IMAGE_NAME:TAG_VERSION
```
and then check the result on status page.

## Using published image

The published  image can be used by logged GitHub users as usual images from DockerHub, 
for example by using the image specification in `docker-compose.yaml` file

```yaml
image: docker.pkg.github.com/prinz-nussknacker/prinz/IMAGE_NAME:TAG_VERSION
```