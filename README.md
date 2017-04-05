![MyWarp](https://github.com/MyWarp/MyWarp/wiki/images/logo_vertical.png)
==========
[![Build Status](https://travis-ci.org/MyWarp/MyWarp.svg?branch=master)](https://travis-ci.org/MyWarp/MyWarp) [![Crowdin](https://d322cqt584bo4o.cloudfront.net/mywarp/localized.svg)](https://crowdin.com/project/mywarp)

MyWarp is an extension for the Minecraft multiplayer that allows players to share locations with each other. Shared locations (called ‘warps’) can be visited instantly by other players using a simple command. Creators remain in full control over their warps: They can remove them, change their locations, or invite players to or uninvite players from using them.

MyWarp’s goal is to create a highly flexible system that allows players to dynamically connect with each other while beeing entirely self-managed. Once set up, administrative interventions are reduced to a bare minimum.

Compiling
---------

You can compile MyWarp as long as you have the [Java Development Kit (JDK)](http://www.oracle.com/technetwork/java/javase/downloads/index-jsp-138363.html) for Java 8 or newer. You only need one version of JDK installed.

The build process uses Gradle, which you do *not* need to download. MyWarp is a multi-module project with two modules:

* `mywarp-core` contains MyWarp
* `mywarp-bukkit` is the Bukkit plugin

### To compile...

#### On Windows

1. Shift + right click the folder with MyWarp's files and click "Open command prompt".
2. `gradlew build`

#### On Linux, BSD or macOS

1. In your terminal, navigate to the folder with MyWarp's files (`cd /folder/of/mywarp/files`)
2. `./gradlew build`

### Then you will find...

You will find:

* The core MyWarp API in **mywarp-core/build/libs**
* MyWarp for Bukkit in **mywarp-bukkit/build/libs**

If you want to use MyWarp, use the `-all` version which includes MyWarp and all necessary libraries.

Contributing
------------

Please read [CONTRIBUTING.md](CONTRIBUTING.md) for important guidelines to follow.

Links
-----
* [Official Website](https://mywarp.github.io/)
* [Documentation](https://github.com/MyWarp/MyWarp/wiki)
* [Localization](https://crowdin.com/project/mywarp)
* [Issue Tracker](https://github.com/MyWarp/MyWarp/issues)
* [Continues Integration](https://thee140.ci.cloudbees.com/)
