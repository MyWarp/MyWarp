# ![MyWarp](https://github.com/MyWarp/MyWarp/wiki/images/logo_vertical.png)
[![Build Status](https://travis-ci.org/MyWarp/MyWarp.svg?branch=master)](https://travis-ci.org/MyWarp/MyWarp)
[![Maintainability](https://api.codeclimate.com/v1/badges/492f3d16610c8c344cd3/maintainability)](https://codeclimate.com/github/MyWarp/MyWarp/maintainability) [![jitpack](https://jitpack.io/v/MyWarp/mywarp.svg)](https://jitpack.io/#MyWarp/mywarp)  [![Crowdin](https://d322cqt584bo4o.cloudfront.net/mywarp/localized.svg)](https://crowdin.com/project/mywarp)

MyWarp is an extension for the Minecraft multiplayer that allows players to create and share warps with each other.

* Create public warps, usable by everybody or private ones, only usable by the creator and invited players.
* Profit from intelligent matching mechanisms that complete warp names while typing.
* Use buttons or pressure plates to access warps.
* Limit the number of warps a user can create: per type and even per world.
* Charge users for creating, managing or using warps.
* Make users wait before or after being teleported.
* Translate or fine-tune every message.

## Project Structure
Starting with version 3, MyWarp has been split into a platform agnostic core and platform-specific implementation. `mywarp-core` contains most of MyWarp's inner logic, e.g. it handles access to and storage of warps, services like timers, limits or economy support and also individual commands.

To use MyWarp, the core has to be implemented for a certain platform. Currently, `mywarp-bukkit` is the only official implementation and targets various implementations of the Bukkit API. An implementation for Sponge is planned.

## Compiling

The project is written for Java 8 and build with [Gradle](http://gradle.org/). To compile, clone this repository and run:

    gradlew build

If you want to build against MyWarp, javadocs and maven dependencies can be found on [JitPack](https://jitpack.io/#MyWarp/mywarp).

### Publishing Artifacts

Build artifacts can be published on [mywarp.github.io/builds](https://mywarp.github.io/builds/) using the `gitPublishPush` task. This requires access to the repo of the website. Additionally, the build number has to be set in order for the build to be displayed correctly.

Publication is typically handled automatically by a CI.

### Customizing the Build

The build can be customized via environment variables or project properties (`-Pkey=value`):

|Key|Value|
|---|-----|
|`BUILD_NUMBER`|The number of the build relative to the current system.|
|`CI_SYSTEM`|The name of the CI system that runs the build.|
|`COMMIT_HASH`|The short hash of the current commit. If not set, the build process attempts to resolve this automatically.|
|`CROWDIN_KEY`|Crowdin's [API key](https://support.crowdin.com/api/api-integration-setup/). If present, translations will be downloaded from crowdin and shaded into the JAR.|
|`GRGIT_USER`|A [GitHub token](https://help.github.com/en/articles/creating-a-personal-access-token-for-the-command-line#creating-a-token) that provides access to the repository artifacts are published in. To set via project property, use `org.ajoberstar.grgit.auth.username` as key.|

## Contributing

Please check [CONTRIBUTING.md](CONTRIBUTING.md) for guidelines to follow. Note that branches in this repository, as well as all pull-requests, are built automatically via [Travis](https://travis-ci.org/MyWarp/MyWarp).

## Links
* [Project Website](https://mywarp.github.io/)
* [Issue Tracker](https://github.com/MyWarp/MyWarp/issues)
* [End-User Documentation](https://github.com/MyWarp/MyWarp/wiki)
* [Localization Management](https://crowdin.com/project/mywarp)
