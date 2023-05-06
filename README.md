<h1><picture>
  <source media="(prefers-color-scheme: dark)" srcset="https://raw.githubusercontent.com/wiki/MyWarp/MyWarp/images/logo_darkmode.png">
  <img alt="MyWarp" src="https://raw.githubusercontent.com/wiki/MyWarp/MyWarp/images/logo_default.png">
</picture></h1>

MyWarp is an extension for Minecraft (Spigot API) that allows players to create and share warps. It is highly
customizable,
fully localized and supports economy plugins.

[![Build Status](https://img.shields.io/github/actions/workflow/status/MyWarp/MyWarp/build.yml?branch=master)](https://github.com/MyWarp/MyWarp/actions)
[![Crowdin](https://d322cqt584bo4o.cloudfront.net/mywarp/localized.svg)](https://crowdin.com/project/mywarp)
[![Latest Release](https://img.shields.io/github/v/release/MyWarp/MyWarp)](https://github.com/MyWarp/MyWarp/releases)
[![Downloads on BukkitDev](https://img.shields.io/badge/dynamic/json?label=downloads&query=downloads.total&url=https%3A%2F%2Fapi.cfwidget.com%2F33546)](https://dev.bukkit.org/projects/mywarp)
[![License](https://img.shields.io/github/license/MyWarp/MyWarp)](LICENSE.txt)

## Features

* Create public and private warps.
* Invite players or whole permission groups to warps.
* Use buttons or pressure plates to teleport to warps.
* Limit the number of warps players can create via permissions.
* Charge users for creating or using warps (via Vault).
* Use cooldowns and warmups for warps.
* Translate or change every message that your players might see.
* Store warps in an SQL-Database (H2, SQLite, MySQL).
* Supports every Bukkit version from 1.7.10 to current.

## Installation & Usage

Download the latest release from [GitHub](https://github.com/MyWarp/MyWarp/releases). Place the jar file in your
server's `plugins` folder and restart the server. MyWarp will generate all necessary files and you can start creating
and using warps. Check the [documentation](https://github.com/MyWarp/MyWarp/wiki) for information on permissions and
other options.

## Getting Help

Please check the [documentation](https://github.com/MyWarp/MyWarp/wiki) for information on how to use and configure
MyWarp. If you have a question or encounter an issue, please open an [issue](https://github.com/MyWarp/MyWarp/issues).

## Contributing

At this point, MyWarp is considered feature complete. We are still open for PRs, but you may want to open an issue
first to discuss any changes. See [CONTRIBUTING](CONTRIBUTING.md) for guidelines to follow, as well as some
comments on the project's structure.

## Build

MyWarp is built using Gradle, targeting Java 8. To compile, clone this repository and run:

    gradlew build

### Publishing Artifacts

Build artefacts can be published on [mywarp.github.io/builds](https://mywarp.github.io/builds/) using the
`gitPublishPush` task. This requires access to the repo of the website. Additionally, the build number has to be set in
order for the build to be displayed correctly.

For the `master` branch, the publication is handled automatically by the CI.

### Customizing the Build

The build can be customized via environment variables or project properties (`-Pkey=value`):

|Key|Value|
|---|-----|
|`BUILD_NUMBER`|The number of the build relative to the current system.|
|`CI_SYSTEM`|The name of the CI system that runs the build.|
|`COMMIT_HASH`|The short hash of the current commit. If not set, the build process attempts to resolve this automatically.|
|`CROWDIN_KEY`|Crowdin's [API key](https://support.crowdin.com/api/api-integration-setup/). If present, translations will be downloaded from crowdin and shaded into the JAR.|
|`GRGIT_USER`|A [GitHub token](https://help.github.com/en/articles/creating-a-personal-access-token-for-the-command-line#creating-a-token) that provides access to the repository artefacts are published in. To set via project property, use `org.ajoberstar.grgit.auth.username` as key.|

## License

MyWarp is available under [GPL3](LICENSE.txt).
