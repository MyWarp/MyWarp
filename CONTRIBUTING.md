Contributing
============

Thank you for your interest in contributing to MyWarp! We appreciate your effort, but to make sure that the inclusion of your patch is a smooth process, we ask that you make note of the following guidelines.

Please note that MyWarp is targeted at **Java 8**.

All contributions must be licensed under the GNU General Public License v3.

Coding Style
---------
MyWarp follows the [Google coding conventions](https://google.github.io/styleguide/javaguide.html) with a few modifications:

1. The column limit is set to 120 characters.
2. All files must have the license header that can be found in `config/checkstyle/header.txt`.
3. The `@author` tag in java-docs is forbidden.

The build process automatically checks most of these conventions using Checkstyle.

>**Note:** You can use our code styles for [Eclipse](https://code.google.com/p/google-styleguide/source/browse/trunk/eclipse-java-google-style.xml) or [IntelliJ IDEA](https://code.google.com/p/google-styleguide/source/browse/trunk/intellij-java-google-style.xml) to let your IDE format the code correctly for you.


Code Conventions
---------
* Use `java.util.Optional` instead of returning `null`.
* Method parameters accepting `null` must be annotated with `@javax.annotation.Nullable`, all methods and parameters are nonnull by default.
* Use `java.util.Objects` for null- and argument checking.
* Use `MyWarpLogger.getLogger(Class<?>)` to create per-class loggers, if needed.
