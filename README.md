# ![MyWarp](https://github.com/MyWarp/MyWarp/wiki/images/logo_vertical.png)
[![Build Status](https://github.com/MyWarp/MyWarp/workflows/build/badge.svg)](https://github.com/MyWarp/MyWarp/actions) [![jitpack](https://jitpack.io/v/MyWarp/mywarp.svg)](https://jitpack.io/#MyWarp/mywarp)  [![Crowdin](https://d322cqt584bo4o.cloudfront.net/mywarp/localized.svg)](https://crowdin.com/project/mywarp)

MyWarp is an extension for the Minecraft multiplayer that allows players to create and share warps with each other.

* Create public and private warps.
* Invite players or whole permission groups to warps.
* Use buttons or pressure plates to teleport to warps.
* Limit the number of warps players can create via permissions.
* Charge users for creating or using warps (via Vault).
* Use cooldowns and warmups for warps.
* Translate or change every message that your players might see.
* Store warps in an SQL-Database (H2, SQLite, MySQL).
* Supports every Bukkit version from 1.7.10 to current.

MyWarp is highly customisable, enable only what you need. Disabled functions will never impact your server’s
performance.

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

Please check [CONTRIBUTING.md](CONTRIBUTING.md) for guidelines to follow. Note that branches in this repository, as well as all pull-requests, are built automatically via [Github Actions](https://github.com/MyWarp/MyWarp/actions).

## Links
* [Project Website](https://mywarp.github.io/)
* [Issue Tracker](https://github.com/MyWarp/MyWarp/issues)
* [End-User Documentation](https://github.com/MyWarp/MyWarp/wiki)
* [Localization Management](https://crowdin.com/project/mywarp)
