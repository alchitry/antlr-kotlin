# ANTLR Kotlin

[![Release](https://jitpack.io/v/alchitry/antlr-kotlin.svg)](https://jitpack.io/#alchitry/antlr-kotlin)
[![License Apache-2.0](https://img.shields.io/badge/license-Apache--2.0-blue.svg)](LICENSE)

This project is a major overhaul of the [original repository](https://github.com/Strumenta/antlr-kotlin). If you are looking for a way to add a Kotlin target to your ANTLR project, go to the [original repository](https://github.com/Strumenta/antlr-kotlin).

This version strips away the non-JVM targets from the runtime. 
The ParserTreeListener was changed to use all suspending methods.
This allows for suspending methods to be used during tree walks.

The ability to add listeners to the parser during the initial parse was removed to keep this from having to all be
suspending methods.

This is used by [Alchitry Labs V2](https://github.com/alchitry/Alchitry-Labs-V2)

---

This project contains everything is needed to support Kotlin as a target for ANTLR. 
It means:

* the **Kotlin target**: this is a plugin for the ANTLR code generator that produces lexers, parsers, and listeners
* the **Kotlin runtime library**: this is a multi-platform library needed to run the lexers and parsers

Because we produce multi-platform code and a multi-platform runtime, the code written using the Kotlin target for ANTLR can run on the JVM (including Android), in the browser, and natively on Mac, Windows, Linux, and iOS.

## Status

The project should be considered experimental. Several parsers have been implemented and work so far using this target but not all the features are complete and well tested.

## Using ANTLR-Kotlin with Gradle

ANTLR-Kotlin is accessible on Maven & Gradle through Jitpack. In jitpack basically you can use every commit or tag as a version number. You can find recent versions on the [Jitpack page for ANTLR-Kotlin](https://jitpack.io/#com.strumenta/antlr-kotlin).

In older versions of this Plugin (antlr-kotlin-plugin <0.0.5), the configuration was applied by the plugin.
Starting from version 0.0.5, you have to apply the configuration manually:

For an example how to use ANTLR-Kotlin see the [JVM Example Project](antlr-kotlin-examples-jvm/build.gradle.kts).
There is also an [Kotlin Multiplatform Project Example](antlr-kotlin-examples-mpp/build.gradle.kts).

## The Kotlin target

The Kotlin target is a plugin for the ANTLR generator. It should be added to the classpath used to run the ANTLR Tool.
You will simply need to specify the language to be `Kotlin`.

## The Kotlin runtime library

This project contains the runtime for the Kotlin target of ANTLR. This runtime is built as a multi-platform project
running on the JVM, JS, and Kotlin/Native platforms.

You will need to use ANTLR 4 and the Kotlin target to generate ANTLR Lexers and Parsers in Kotlin. 
