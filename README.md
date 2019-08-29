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

    gradlew check

If you want to build against MyWarp, javadocs and maven dependencies can be found on [JitPack](https://jitpack.io/#MyWarp/mywarp).

## Contributing

Please check [CONTRIBUTING.md](CONTRIBUTING.md) for guidelines to follow. Note that branches in this repository, as well as all pull-requests, are built automatically via [Travis](https://travis-ci.org/MyWarp/MyWarp).

## Links
* [Project Website](https://mywarp.github.io/)
* [Issue Tracker](https://github.com/MyWarp/MyWarp/issues)
* [End-User Documentation](https://github.com/MyWarp/MyWarp/wiki)
* [Localization Management](https://crowdin.com/project/mywarp)
