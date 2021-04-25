# Release process

Release is automated by GitHub actions workflow configured in [release.yaml]({{ book.sourcesRootUrl }}.github/workflows/release.yaml)
file. It contains two jobs i.e. releasing current version to GitHub packages repository and creating a GitHub release
with sources from release version.

To trigger release process one should push the tagged git commit to `master` branch with tag having the `v` prefix.
Description of GitHub release has to be added manually after automatic release process but the version of release
is taken from git tag.

In order to keep everything in order one have to follow [Semantic Versioning](https://semver.org/)
rules. Additionally, there are `SNAPSHOT` releases that are published before official release and can be updated before
the official release process.
