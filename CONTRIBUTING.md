# Contributing

Thanks for your interest in contributing to MyWarp. Below you may find some general information to get you started.

## General

* Never use platform-specific code in the platform-agnostic core (see below)!
* MyWarp is targeted at Java 8. For this reason, dependencies that require newer versions cannot be used.
* MyWarp needs to run on any Minecraft version from `1.7.10` onwards. The implementation needs to work around API
  changes.
* The code includes extensive (but not that helpful) Javadocs, but, unfortunately, zero automated tests.
* All contributions must be licensed under GPL3.

## Project Structure

Starting with version 3, MyWarp has been split into a platform-agnostic core and platform-specific implementation.
`mywarp-core` contains most of MyWarp's inner logic, e.g. it handles access to and storage of warps, services like
timers, limits or economy support and also individual commands.

To use MyWarp, the core has to be implemented for a platform. Currently, 'mywarp-bukkit' is the only implementation
and targets various implementations of the Bukkit API. An implementation for Sponge was planned, but never finished.

## Coding Style

We use the [Google coding conventions](https://google.github.io/styleguide/javaguide.html) with minor changes:

1. The column limit is set to 120 characters.
2. All files must have the license header that can be found in `config/checkstyle/header.txt`.
3. The `@author` tag in java-docs is forbidden.

You can use code styles
for [Eclipse](https://code.google.com/p/google-styleguide/source/browse/trunk/eclipse-java-google-style.xml)
or [IntelliJ IDEA](https://code.google.com/p/google-styleguide/source/browse/trunk/intellij-java-google-style.xml) to
let your IDE format the code correctly for you.

## Conventions

* Use `java.util.Optional` instead of returning `null`.
* Method parameters accepting `null` must be annotated with `@javax.annotation.Nullable`, all methods and parameters are nonnull by default.
* Use `java.util.Objects` for null- and argument checking.
* Use `MyWarpLogger.getLogger(Class<?>)` to create per-class loggers, if needed.
