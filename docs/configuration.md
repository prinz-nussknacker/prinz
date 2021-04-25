# Project configuration

## Project environment

Main configuration needed to run the docker images is located in `.env` file
which is read by `sbt` at the beginning of loading project configuration.

Additionally, Prinz uses GitHub Packages repository to release and resolve its
dependencies. This requires having `GITHUB_TOKEN` which gives user access ti GitHub
registry. You can generate your token in your [GitHub settings](https://github.com/settings/tokens).
Generated token can be added to configuration in two ways:

1. Saved as environment variable so it can be easily used when building the project
   using `sbtwrapper` from command line. This type of configuration is mainly used
   on CI environment where the `GITHUB_TOKEN` environment variable is defined
   mostly automatically. Adding single line to your `~/.bashrc` or `~/.bash_profile`
   configuration and souring it to terminal is enough to have working build locally
   but only in terminal builds (as the IDE seems to load user environment variables
   from these files for sbt builds).
```shell
export GITHUB_TOKEN="your-generated-token-value-here"
```

2. Added to git configuration and used by the IDE when loading the project. This way
   is preferred on local dev environment in order to have the ability to download dependencies
   from GitHub Packages repository. It's enough to provide your access token by placing it
   at the end of your git configuration file located by default in `~/.gitconfig` by adding
   extra configuration lines
```
[github]
	token = your-generated-token-value-here
```
