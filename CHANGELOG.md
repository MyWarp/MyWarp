# Change Log

All notable changes to this project will be documented in this file.
Starting with version 3, this project adheres to [Semantic Versioning](http://semver.org/).

Unreleased APIs may change dramatically between `SNAPSHOT` versions.

## Unreleased

### Fixed
* [Bukkit] Properly cancel physical interaction events in order to fix rare `Player moved wrongly!` errors
* [Bukkit] [[#205](https://github.com/TheE/MyWarp/issues/205)] - Restore compatibility with Essential's `/back` command

## [3.0.1] - 2019-02-19

### Changed
* [Bukkit] Update VaultAPI to 1.7 hosted in our own repository

### Fixed
* [Bukkit][[#196](https://github.com/TheE/MyWarp/issues/196)] - Warps on blocks added in 1.13 are marked unsafe even if they are safe
* [Bukkit][[#198](https://github.com/TheE/MyWarp/issues/198)] - Translation files are missing from build

## [3.0] - 2019-01-01

### Changed
* [Bukkit][[#191](https://github.com/TheE/MyWarp/issues/191)] - Increase the conversation timeout to 60 seconds
* [[#191](https://github.com/TheE/MyWarp/issues/191)] - Make warp numbers in the `assets` command translatable
* [Bukkit] - Build against Bukkit 1.13.2-R0.1-SNAPSHOT
* Update Gradle to 5.0 and use up-to-date versions of all plugins used in the build process

### Fixed
* [[#181](https://github.com/TheE/MyWarp/issues/181)] - Warp limits fail to account for warps on multiple worlds
* [[#192](https://github.com/TheE/MyWarp/issues/192)] - Using MySQL 5.7 or newer raises unsupported database errors

## [3.0-rc-1] - 2018-09-05

### Changed
* [[#143](https://github.com/TheE/MyWarp/issues/143)] - Change group-id and package structure to `io.github.mywarp.mywarp`
* [[#71](https://github.com/TheE/MyWarp/issues/71)] - Require Java 8 to run MyWarp, update internals to use new, native Java constructs where possible
* Update JOOQ to 3.11
* Update FlyWay to 4.2.0
* [Bukkit] Build against Bukkit 1.13.1-R0.1-SNAPSHOT
* [[#151](https://github.com/TheE/MyWarp/issues/151)] - Use new crowdin API
* [Spigot][[#118](https://github.com/TheE/MyWarp/issues/118)] - Use `player.getLocale()` if it exists
* [[#160](https://github.com/TheE/MyWarp/issues/160)] - Move LocaleResolver to VersionSupport
* [[#144](https://github.com/TheE/MyWarp/issues/140)] - Make DBMS configuration way more flexible
* Update Gradle to 4.8 and use up-to-date versions of all plugins used in the build process
* Groups in `/warp invite` and `/warp uninvite` have to be prefixed by `g:` rather than using the `-g` flag.
* [[#185](https://github.com/TheE/MyWarp/issues/185)] - Store all strings as `UTF8MB4` on MySQL
* [[#145](https://github.com/TheE/MyWarp/issues/145)] - Resolve player-names and identifiers asynchronously
* Move user downloadable development builds to [mywarp.github.io](https://mywarp.github.io/builds.html).
* [Bukkit][[#187](https://github.com/TheE/MyWarp/issues/187)] - Update H2 to 1.4
* [Bukkit][[#178](https://github.com/TheE/MyWarp/issues/178)] - Add a way to specify JDBC configuration options
* [Bukkit][[#189](https://github.com/TheE/MyWarp/issues/189)] - Use MaterialData when checking for buttons and levers, improving forward compatibility
* [[#146](https://github.com/TheE/MyWarp/issues/146)] - Optionally allow platforms to use their own service to display paginated content

### Added
* [[#142](https://github.com/TheE/MyWarp/issues/142)] - Add an option to disallow warps with different capitalization
* Accept UUIDs in `/warp invite`, `/warp uninvite` and `/warp give` using the `u:` prefix
* [[#176](https://github.com/TheE/MyWarp/issues/176)] - Validate compatibility with older versions of Bukkit and Guava using AnimalSniffer
* [Bukkit][[#189](https://github.com/TheE/MyWarp/issues/189)] - Resolve material information from a bundled configuration file, rather than hard-coding them

### Fixed
* [[#148](https://github.com/TheE/MyWarp/issues/148)] - `/warp list` flags are not working
* [[#153](https://github.com/TheE/MyWarp/issues/153)] - Invalid date formatting in `/warp info`
* [[#150](https://github.com/TheE/MyWarp/issues/150)] - slf4bukkit does not work on PaperSpigot
* [[#157](https://github.com/TheE/MyWarp/issues/157), [[#61](https://github.com/TheE/MyWarp/issues/161)] - Issues with line endings ìn messages on Windows based servers
* [[#166](https://github.com/TheE/MyWarp/issues/166), [[#167](https://github.com/TheE/MyWarp/issues/167)] - Dynmap shows public and private warps
* [[#173](https://github.com/TheE/MyWarp/issues/173)] - Fix calculation of visits per day for just created warps
* [[#156](https://github.com/TheE/MyWarp/issues/156)] - Fix teleporting with donkeys and mules on 1.12
* Fix Guava compatibility of the Dynmap bridge
* Fix handling of manually thrown AuthorizationExceptions in commands
* [[#179](https://github.com/TheE/MyWarp/issues/179)] - Fix `give.is-owner` error message
* [[#182](https://github.com/TheE/MyWarp/issues/182)] - Fix warp signs when warp names are not handled case sensitive
* [Bukkit] Fix incompatibilities with Bukkit 1.7.10


### Removed:
* Remove `%creator%` placeholder from welcome messages and Dynmap markers
* [Bukkit][[#146](https://github.com/TheE/MyWarp/issues/146)] - Remove explicit position correction for warps created in MyWarp 2 (the default warp safety algorithm is now responsible for handling these)
* Remove dependency on commons-lang

## [3.0-beta-4] - 2017-02-26

### Changed:
* [[#64](https://github.com/TheE/MyWarp/issues/64)] - Decouple authentication from the Warp implementation
* Update JOOQ to 3.6.4
* Make `DynamicMessages` use the bundled localizations by default
* Commands are handled by the core, not by individual implementations
* Rename `WarpRemovalEvent` to `WarpDeleteEvent`
* Decouple teleport from Warp implementation
* [[#74](https://github.com/TheE/MyWarp/issues/74)] - Update Intake to 4.0
* Construct `MyWarp` with a factory rather than a constructor
* [Bukkit] Overhaul Bukkit implementation
* Unify naming of internal components, refactor most classes into a cleaner package structure
* Make the core handle teleports of tamed horses
* [[#108](https://github.com/TheE/MyWarp/issues/108)] - Update FlyWay to 4.1.1
* [[#86](https://github.com/TheE/MyWarp/issues/86)] - Replace EulerDirection with Vectors
* Require at least Java8 to build (not to run) MyWarp
* [[#84](https://github.com/TheE/MyWarp/issues/84)] - Warp signs are handled completely by the core
* `Warp.Type` can no longer be converted to a `Limit.Type`
* Remove Profiles, player UUIDs need to be converted to names by using the cache directly
* Players are informed about invitations, including group invitations, via events (can be turned of in the configuration)
* [Bukkit] Use the new official Vault repository
* Move population and depopulation from WarpManager into a separated (hidden) interface
* [[#93](https://github.com/TheE/MyWarp/issues/93)] - Allow interactions (e.g. updating, deleting, listing) with warps positioned on non existing worlds
* [Bukkit] [[#117](https://github.com/TheE/MyWarp/issues/117)] - Update warp safety with Materials added in Minecraft 1.8, 1.9, 1.10 and 1.11
* Update Gradle to 3.5 and use up-to-date versions of all plugins used in the build process
* Move handling of teleport and welcome messages into the `TeleportService`
* Massively simplify internal implementation of limits
* Update flow-math to 1.0.3

### Added:
* [[#77](https://github.com/TheE/MyWarp/issues/77)] - Support for golden and iron pressure plates
* [[#65](https://github.com/TheE/MyWarp/issues/65), [#85](https://github.com/TheE/MyWarp/issues/85)] - Platforms can define optional capabilities to be used by the core if available; implement economy, limits, teleport safety and timers are such capabilites
* [[#83](https://github.com/TheE/MyWarp/issues/83)] - Tab complete for Warps, online and offline players
* [[#109](https://github.com/TheE/MyWarp/issues/109)] - Automatically download localizations from crowdin upon each build

### Fixed:
* [[#81](https://github.com/TheE/MyWarp/issues/81), [#78](https://github.com/TheE/MyWarp/issues/78)] - Warmup does not abort on damage or move
* Use the default Locale if the player's client returns an invalid one
* [[#98](https://github.com/TheE/MyWarp/issues/98)] - Profile cache updates in main thread cause server lag
* [[#99](https://github.com/TheE/MyWarp/issues/99)] - Checks for warp-creation do not work
* [[#106](https://github.com/TheE/MyWarp/issues/106)] - Command exception without cause results in NPE
* [Bukkit] [[#104](https://github.com/TheE/MyWarp/issues/104)] - Creation of plugin folder fails
* Matching warp names results in `IndexOutOfBoundException`
* [[#102](https://github.com/TheE/MyWarp/issues/102)] - Malformatted value bundles can crash the server
* Teleport effect setting has no effect
* `/warp assets` does not display limits of zero
* [[#92](https://github.com/TheE/MyWarp/issues/92)] - Insufficient permissions for the `/warp` command
* [Bukkit] [[#105](https://github.com/TheE/MyWarp/issues/105)] - Warps only shown on DynMap until restart
* [Bukkit] [[#116](https://github.com/TheE/MyWarp/issues/116)] - Player locales are parsed incorrectly when running on Minecraft 1.11
* [Bukkit] [[#130](https://github.com/TheE/MyWarp/issues/130)] - Player name to UUID conversion fails if a name is requested twice
* [[#132](https://github.com/TheE/MyWarp/issues/132)] - CI builds are missing translations from crowdin
* [Bukkit] [[#136](https://github.com/TheE/MyWarp/issues/136)] - Warp labels on Dynmap display the creators UUID instead of the name
* NoSuchPlayerException`s try to display a player's name before falling back to its UUID

### Removed:
* [Bukkit] Chunk pre-loading can no longer be disabled

## [3.0-beta-3] - 2015-09-06

### Fixed:
* [[#58](https://github.com/TheE/MyWarp/issues/58)] - Connection timeout on MySQL
* [[#53](https://github.com/TheE/MyWarp/issues/53)] - Ownership change of a single warp affects all warps of a player

## [3.0-beta-2] - 2015-06-29

### Added:
* Added delay to the sever shutdown if changes are still written to the database
* [[#48](https://github.com/TheE/MyWarp/issues/48)] - Added support for H2
* [[#50](https://github.com/TheE/MyWarp/issues/50)] - Added support for MariaDB
* [[#33](https://github.com/TheE/MyWarp/issues/33)] - Add manual addition or overwriting of bundled localizations

### Changed:
* [[#29](https://github.com/TheE/MyWarp/issues/29)] - Platform implementations completely manage the `DataSource`
* Replace `DataConnection` with `WarpStorage`
* Overhaul import from other databases
* Overhaul SQL queries, remove overhead and use transactions
* Internally represent currencies as `BigDecimal`
* Overhaul `WarpManager` implementation, directly expose the `EventBus` in `MyWarp`
* Overhaul `EconomyManager` definition and functionality
* Replace `TeleportService` with `TeleportManager`
* Move token functionality from `Warp` to `WarpUtils`

### Fixed:
* Fixed `/warp player` being unusable by non-players
* Fixed NPE thrown when formatting special characters
* Fixed invalid variables in German (un)invite messages for public warps
* Fixed `/warp assets` being completely empty for players without warps
* [[#43](https://github.com/TheE/MyWarp/issues/43)] - Warps with invitations are not deleted from the database
* [[#47](https://github.com/TheE/MyWarp/issues/47)] - Economy support not working
* [[#49](https://github.com/TheE/MyWarp/issues/49)] - Irregular handling of AuthorizationExceptions for `/warp <warp>`
* [[#46](https://github.com/TheE/MyWarp/issues/46)] - Incompatibilities with recent Guava versions

### Removed:
* [Bukkit] Removed support for SQLite 3.7.x witch is bundled by CraftBukkit.

## [3.0-beta-1] - 2015-04-23

### Added:
* Added support for UUIDs
* Added warp creation date storage and use it, together with the visist counter, to determine a warp's popularity
* Added `-n <query>` flag for the list command to list warp with a name matching the query
* Added `-r <radius>` flag for the list command to list warps within the given radius
* Added a way to display a warp's information before accepting it when given by another user
* Added `/to <warp>` command to quickly access warps
* Added commit hash and build number to the version number
* Added `-f` flag for invitation and uninvitation to ignore all warp limits

### Changed:
* Rewrote everything from scratch, split platform neutral core from and platform specific implementation
* The database layout has been changed and is now fully normalized, old warps need to imported per command
* Use JOOQ for all database communication
* Build against Bukkit 1.7.9-R0.2
* Build against VaultAPI 1.4 to support UUIDs
* Use FlyWay to keep the database scheme up-todate
* Use multiple Java ressource bundles backed by properties files for localizations. They are no longer user editable.
* Switch to Gradle to handle the build process
* Use Intake to parse Commands
* Use `group.[GROUPNAME]` to resolve groups using SuperPerms. Use meta-permissions on PEX instead of the native API
* Use slf4j for logging
* Overhaul permissions completely
* Overhaul configuration values

### Fixed:
* Players are no longer able to create a warp with a name longer than 32 characters
* Usage of `/warp public` and `/warp private` by others can no longer exceed the creator's warp limit unless intended

### Removed:
* Removed warp type symbol from the warp listing
* Removed `/warp accept` in favor of a conversation.
* Removed automatic renaming of `homes-warp.db` (hmod)
* Removed `/warp search` in favor of `/warp list -n <query>`
* Removed the `-p` from the list command
* Removed teleporting of leashed entities
* Removed configuration option to disable suggestion of popular warps
* Removed formatting options for welcome-messages and localizations

## [2.6.5] - 2014-04-11

### Added
* Check if MySQL connection is still valid before using it

### Changed
* If a localization key is missing, display the key and log a warning instead of throwing an exception
* Use `&` as a color character in localization messages, to allow coloring welcome-messages in-game
* Exclude all commands a user cannot use from the help menu
* `/warp assets` no longer displays limits that are overwritten

### Fixed
* Fixed localization file encoding on Windows that made special characters unusable
* Fixed per-world limit-disobey permissions for players who can disobey some, but not all worlds in a limit
* Fixed some typos in the German localization
* Fixed `/warp assets` with zero warps throwing an exception

## [2.6.4] - 2014-12-24

### Added
* Added a note about the per-command help in the help menu
* Added another color for owned, public warps to the warp list
* Added optional per player-localizations using the client locale
* Added removal of welcome-messages
* Added 1.7 blocks to warp safety
* Added per-world warp limits

### Changed
* Made the `-?` flag call the command help instead of a simple `?`
* Completely rewrote the localization support, now makes use of YAML resource bundles instead of plain text files
* Rewrote the location safety algorithm to check the closest blocks first
* Disallow command aliases as warp names
* Changed the formatting of the search command
* Made the import command somewhat less spammy

### Fixed
* Fixed missing warps in Dynmap
* Fixed an odd logic in the give command that might have interfered with permission checks
* Fixed an unnecessary database call when creating warps

## [2.6.3] - 2013-08-18

### Added
* Added experimental support for teleporting leashed entities (disabled by default)
* Added workaround for [BUKKIT-4365](https://bukkit.atlassian.net/browse/BUKKIT-4365) (players are unable to use warps in other worls via pressure plates)

### Changed
* Improved SQL connection speed (especially for large servers)
* Rewrite warmups and cooldowns to use less memory

### Fixed
* Fixed unnecessary saving of welcome messages when the warp was loaded from the database
* Fixed teleporting of non-tamed horses
* Fixed crash when Vault is installed but not properly registered
* Fixed crash when using third-party plugin reloaders
* Fixed unwanted Dynmap markers for private warps
* Fixed possible crash caused by of too many open threads

## [2.6.2] - 2013-07-29

### Added
* Added `/warp assets [player]` command to list a player's limits
* Added `stats` alias to `/warp info [warp]`
* Added support for teleporting players with their horses
* Added an option to turn off warp signs completely
* Added an option to modify identifiers for warp signs

### Fixed
* Fixed `config.yml` creation

### Removed
* Removed outdated `/warp alist` command

## [2.6.1] - 2013-06-23

### Added
* Added `/warp info` command

### Changed
* Internal project-design overhaul
* Made `/warp give` ask users if they want accept warps - can be overruled via flags
* Made Dynmap label text completely configurable (supports html)

### Fixed
* Fixed economy-support for warp signs
* Fixed charging users for using a warp when the teleport could still fail
* Fixed missing translation for the warp command

## [2.6] - 2013-04-17

### Added
* Added `%player%` as variable for invitations
* Added filtering per world for the list command
* Added `-p` flag for listing and searching, sorts results by popularity
* Added `-f` flag to the import command to overwrite existing warps
* Added 1.5 blocks to warp-safety
* Added economy support for commands and warp-signs via Vault
* Added the ability to reset a compass so it points back to the spawn point
* Added a timer for the welcome-message changer that cancels the change if no message is provided within 30 seconds

### Changed
* Moved to a new CommandHandler once again, extremely inspired by WorldEdit (adds more checks for individual commands, improves help messages and support for external aliases etc.)
* Rewrote both list commands to use flags instead of prefixes
* Improved formatting of lists

### Fixed
* Fixed missing `error.noPermission.modify` translation
* Fixed warp command putting the player one block above the warp
* Fixed a rare bug were the LanguageManager failed to recognize the encoding of the default files
* Fixed limit checks when privatizing or publicizing a warp
* Fixed database-connections being not thread-safe
* Fixed possible thread-safety issues with the welcome command

## [2.5.2] - 2013-03-09

### Added
* Added a message to inform users if a publicized warp is already public or a privatized warp already private
* Added experimental Dynmap support (let me know how this works or if you need additional features)

### Fixed
* Fixed `/warp public` not taking limits into account
* Fixed sorting of `/warp list`
* Worked around a rare and yet unresolved bug that occurs when loading the default language file

## [2.5.1] - 2013-03-03

### Added
* Added optional warp suggestions based on visits

### Changed
* Added a permission that allows creation of SignWarps for warps owned by other players (`mywarp.warp.sign.create.all`)
* Added a permission for `/warp help` (`mywarp.warp.basic.help`)
* Removed `mywarp.warp.social.(un)invite.player` permission in favor of `mywarp.warp.social.(un)invite`, so that all permissions are completely in line
* Made sure warp safety will work with future Minecraft versions without the need of an update
* Made database-connection use an async-thread so it will never lag the server
* Attempt to make `/warp help` look more like Bukkit's own help
* Minor update to the database layout
* Rewrote a lot of old and inefficient code to be more efficient

### Fixed
* Fixed a potential resource leak inside translation handling
* Fixed not working teleportation when players were inside a vehicle
* Fixed several typos in both translations
* Fixed welcome messages for warps containing `/`

## [2.5] - 2012-12-11

### Added
* Added 1.4 blocks to warp safety
* Added smoke effect when warping
* Added support for buttons, levers and pressure plates to sign warps
* Added possibility for translations
* Added command to import warps from MySQL to SQLite and vice versa
* Added option to disable warp limits
* Added optional system to control warp access per world

### Changed
* Made all commands except warping, warp creation and changing welcome messages ready to be used via console and command block
* Rewrote warp signs completely, they now require `[MyWarp]` on the second line
* Rewrote database connection entirely to cut the need of external libraries
* Added specific permissions for admin tasks, changed global admin permission to `mywarp.admin.*`
* Removed `adminPrivateWarps` option in favor of `mywarp.admin.accessall` and `mywarp.admin.modifyall` permissions
* A lot of minor improvements on the code

### Fixed
* Fixed bug where timers were not marked as finished

## [2.4] - 2012-09-06

### Added
* Added fully optional warmups, cooldowns and needed configuration
* Added field in the database to count how often a warp was visited
* Added simple macro system for welcome messages (`%player%`, `%warp%`, `%creator%`, `%visits%`)
* Added possibility to invite permission groups to a warp (needs either bPermissions, PEX, Groupmanager, Vault or it won't work)

### Changed
* Rewrote permissions again, use Superperms only to define whether a player has a permission or not

### Fixed
* Fixed existing errors for MySQL users where warps higher than 125 caused trouble.

## [2.3] - 2012-08-22 [YANKED]

### Added
* Added vault for permissions (thanks, mung3r) and fixed existing errors
* Added a total warp limit
* Added permission based warp limits
* Expanded list command so it is able to show warps of a given player only
* Added checks to see if warp is safe before teleporting players to it
* Added `/warp update`

### Changed
* Updated to be compatible with CB 1.3.1 and up (async chat stupidity)
* New CommandHandler, should fix several old problems (thanks, mung3r)
* MyWarp downloads needed library automatically (thanks, mung3r)
* Simplified MySQL setup

### Removed
* Removed dependence on external help plugin

### Fixed
* Fixed social commands ignoring limits

## [2.2] - 2012-03-01

### Added
* Support for bPermissions2

### Changed
* Switched to new events handler system

### Fixed
* SuperPerms OPs fix

## [2.1] - 2012-01-26

### Added
* New config flag "opPermissions: true" to enable OPs to use all commands with SuperPerms
* Support for SuperPerms
* Support for bPermissions

### Changed
* Compiled against current CraftBukkit RB
* Overhauled Permissions setup to be more inline with MyHome
* Overhauled other Permissions support
* Major code cleanup

## [2.0.1] - 2011-11-05

### Changed
* Updated dependencies to current versions

### Fixed
* Fixed MySQL complaints about inactive connections

## 2.0 - 2011-06-07

### Added
* MySQL support
* Warp import from `warps.db` to mysql
* Permissions 3.x support

### Changed
* Tested and works on CB928

### Removed
* Removed extremely old artifacts (import of hmod warps)

## 1.10.7

### Added
* Added native support for PermissionsEx

### Fixed
* Fixed namespace

## 1.10.6

### Changed
* Updated for CB786-793
* Updated to Help 3.0 ( not interesting for you until you compile from Lycano's fork on github :p )

## 1.10.5c

### Fixed
* Fixed the `Could not pass event PLAYER_INTERACT to MyWarp` error

## 1.10.5b

### Added
* Added GroupManager support

### Changed
* Updated for CraftBukkit RB `#677` (also tested with CB `#678`)
* Updated for Permissions 2.5.5 or higher (tested with 2.6 and 2.7)
* Changed update URL where MyWarp downloads sqlite libraries

[Unreleased]: https://github.com/TheE/MyWarp/compare/v3.0.1...HEAD
[3.0.1]: https://github.com/TheE/MyWarp/compare/v3.0...v3.0.1
[3.0]: https://github.com/TheE/MyWarp/compare/v3.0-rc-1...v3.0
[3.0-rc-1]: https://github.com/TheE/MyWarp/compare/v3.0-beta-4...v3.0-rc-1
[3.0-beta-4]: https://github.com/TheE/MyWarp/compare/v3.0-beta-3...v3.0-beta-4
[3.0-beta-3]: https://github.com/TheE/MyWarp/compare/v3.0-beta-2...v3.0-beta-3
[3.0-beta-2]: https://github.com/TheE/MyWarp/compare/v3.0-beta-1...v3.0-beta-2
[3.0-beta-1]: https://github.com/TheE/MyWarp/compare/v2.6.5...v3.0-beta-1
[2.6.5]: https://github.com/TheE/MyWarp/compare/v2.6.4...v2.6.5
[2.6.4]: https://github.com/TheE/MyWarp/compare/v2.6.3...v2.6.4
[2.6.3]: https://github.com/TheE/MyWarp/compare/v2.6.2...v2.6.3
[2.6.2]: https://github.com/TheE/MyWarp/compare/v2.6.1...v2.6.2
[2.6.1]: https://github.com/TheE/MyWarp/compare/v2.6...v2.6.1
[2.6]: https://github.com/TheE/MyWarp/compare/v2.5.2...v2.6
[2.5.2]: https://github.com/TheE/MyWarp/compare/v2.5.1...v2.5.2
[2.5.1]: https://github.com/TheE/MyWarp/compare/v2.5...v2.5.1
[2.5]: https://github.com/TheE/MyWarp/compare/v2.4...v2.5
[2.4]: https://github.com/TheE/MyWarp/compare/v2.3...v2.4
[2.3]: https://github.com/TheE/MyWarp/compare/v2.2...v2.3
[2.2]: https://github.com/TheE/MyWarp/compare/v2.1...v2.2
[2.1]: https://github.com/TheE/MyWarp/compare/v2.0.1...v2.1
[2.0.1]: https://github.com/TheE/MyWarp/compare/v2.0...v2.0.1
