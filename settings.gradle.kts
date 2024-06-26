/*
 * This file was generated by the Gradle 'init' task.
 *
 * The settings file is used to specify which projects to include in your build.
 *
 * Detailed information about configuring a multi-project build in Gradle can be found
 * in the user manual at https://docs.gradle.org/7.4.1/userguide/multi_project_builds.html
 * This project uses @Incubating APIs which are subject to change.
 */

rootProject.name = "server"
include("app")

plugins {
    kotlin("jvm") version "2.0.0" apply false
    kotlin("plugin.serialization") version "2.0.0" apply false
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
    versionCatalogs {
        create("libs") {
            version("kotlin", "2.0.0")

            //
            //Ktor
            //
            version("ktor", "2.3.3")
            library("ktor-server-core", "io.ktor", "ktor-server-core").versionRef("ktor")
            library("ktor-server-netty", "io.ktor", "ktor-server-netty").versionRef("ktor")
            library("ktor-server-content-negotiation", "io.ktor", "ktor-server-content-negotiation").versionRef("ktor")
            library("ktor-server-default-headers", "io.ktor", "ktor-server-default-headers").versionRef("ktor")
            library("ktor-server-auto-head-response", "io.ktor", "ktor-server-auto-head-response").versionRef("ktor")
            library("ktor-server-html-builder", "io.ktor", "ktor-server-html-builder").versionRef("ktor")
            library("ktor-server-call-logging", "io.ktor", "ktor-server-call-logging").versionRef("ktor")
            library("ktor-serialization-gson", "io.ktor", "ktor-serialization-gson").versionRef("ktor")
            library("ktor-server-status-pages", "io.ktor", "ktor-server-status-pages").versionRef("ktor")

            bundle("ktor", listOf(
              "ktor-server-core",
              "ktor-server-netty",
              "ktor-server-content-negotiation",
              "ktor-server-default-headers",
              "ktor-server-auto-head-response",
              "ktor-server-html-builder",
              "ktor-server-call-logging",
              "ktor-serialization-gson",
                "ktor-server-status-pages"))

            //
            //Kotlinx
            //
            version("kotlinx", "1.7.3")
            library("kotlinx-coroutines-core", "org.jetbrains.kotlinx", "kotlinx-coroutines-core").versionRef("kotlinx")
            library("kotlinx-serialization-json", "org.jetbrains.kotlinx", "kotlinx-serialization-json").version("1.6.0")

            bundle("kotlinx", listOf("kotlinx-coroutines-core", "kotlinx-serialization-json"))

            //
            // Shared
            //
            version("flogger", "0.7.4")
            library("flogger", "com.google.flogger", "flogger").versionRef("flogger")
            library("flogger-slf4j-backend", "com.google.flogger", "flogger-slf4j-backend").versionRef("flogger")
            library("flogger-backend", "com.google.flogger", "flogger-system-backend").versionRef("flogger")

            library("logback-core", "ch.qos.logback", "logback-core").version("1.4.11")
            library("logback-classic", "ch.qos.logback", "logback-classic").version("1.4.11")

            library("guice", "com.google.inject", "guice").version("7.0.0")

            bundle("shared", listOf("flogger", "flogger-backend", "flogger-slf4j-backend", "logback-core", "logback-classic", "guice"))

            //
            // Quartz
            //
            library("quartz", "org.quartz-scheduler", "quartz").version("2.3.2")

            //
            //persistence
            //
            version("exposed", "0.42.0")
            library("exposed-core", "org.jetbrains.exposed", "exposed-core").versionRef("exposed")
            library("exposed-jdbc", "org.jetbrains.exposed", "exposed-jdbc").versionRef("exposed")
            library("exposed-dao", "org.jetbrains.exposed", "exposed-dao").versionRef("exposed")
            library("exposed-kotlin-datetime", "org.jetbrains.exposed", "exposed-kotlin-datetime").versionRef("exposed")
            library("sqlite-jbdc", "org.xerial", "sqlite-jdbc").version("3.42.0.0")
            library("flywaydb", "org.flywaydb", "flyway-core").version("9.22.3")

            bundle("persistence", listOf(
                "exposed-core",
                "exposed-jdbc",
                "exposed-dao",
                "exposed-kotlin-datetime",
                "sqlite-jbdc",
                "flywaydb"))

            //
            // Testing
            //
            version ("junit", "5.10.0")
            library("junit", "org.junit.jupiter", "junit-jupiter").versionRef("junit")
            library("junit-engine", "org.junit.jupiter", "junit-jupiter-engine").versionRef("junit")

            library("ktor-server-test-host", "io.ktor", "ktor-server-test-host").versionRef("ktor")

            library("kotlin-test", "org.jetbrains.kotlin", "kotlin-test").versionRef("kotlin")
            library("kotlinx-coroutine-test", "org.jetbrains.kotlinx", "kotlinx-coroutines-test").versionRef("kotlinx")

            library("truth", "com.google.truth", "truth").version("1.1.5")
             
            bundle("test", listOf(
                "junit",
                "junit-engine",
                "kotlin-test",
                "kotlinx-coroutine-test",
                "ktor-server-test-host",
                "truth"))

            //
            // Plugins
            //
            plugin("ktfmt", "com.ncorti.ktfmt.gradle" ).version("0.13.0")
        }
    }
}