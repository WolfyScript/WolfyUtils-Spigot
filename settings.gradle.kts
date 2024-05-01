rootProject.name = "wolfyutils-spigot"

pluginManagement {
    repositories {
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()
    }
}

sequenceOf(
    "core",
    "plugin-compatibility",
    "nmsutil",
    "nmsutil:v1_17_R1_P1",
    "nmsutil:v1_18_R1",
    "nmsutil:v1_18_R1_P1",
    "nmsutil:v1_18_R2",
    "nmsutil:v1_19_R3",
    "nmsutil:v1_19_R1",
    "nmsutil:v1_19_R2",
    "nmsutil:v1_20_R1",
    "nmsutil:v1_20_R2",
    "nmsutil:v1_20_R3",
    "nmsutil:v1_20_R4",
).forEach {
    include(":${it}")
    project(":${it}").projectDir = file(it.replace(":", "/"))
}

dependencyResolutionManagement {
    versionCatalogs {
        // Third-party libraries
        create("libs") {
            // versions
            version("adventure", "4.14.0")

            // libs
            library("guice", "com.google.inject:guice:5.1.0")
            library("reflections", "org.reflections:reflections:0.10.2")
            library("javassist", "org.javassist:javassist:3.29.2-GA")
            library("adventure-api", "net.kyori", "adventure-api").versionRef("adventure")
            library("adventure-minimessage", "net.kyori", "adventure-text-minimessage").versionRef("adventure")
            library("adventure-platform-bukkit", "net.kyori", "adventure-platform-bukkit").version("4.1.2")
            library("jackson", "com.fasterxml.jackson.core", "jackson-databind").version("2.14.0-rc1")
            library("fastutil", "it.unimi.dsi", "fastutil").version("8.5.6")
            library("typesafe.config", "com.typesafe", "config").version("1.3.1")
            library("bstats", "org.bstats", "bstats-bukkit").version("3.0.0")
            library("nbtapi-api", "de.tr7zw", "item-nbt-api").version("2.12.4")
            library("nbtapi-data", "de.tr7zw", "nbt-data-api").version("2.12.4")
        }
        // Libraries only used for testing
        create("testLibs") {
            library("junit-jupiter", "org.junit.jupiter:junit-jupiter:5.9.0")
            library("mockito", "org.mockito:mockito-core:4.8.0")
            library("mockbukkit", "com.github.seeseemelk:MockBukkit-v1.18:2.85.2")
        }
        // internal apis
        create("apis") {
            library("wolfyutils", "com.wolfyscript.wolfyutils", "wolfyutilities").version("4.16.1-SNAPSHOT")
            library("dataformat-hocon", "com.wolfyscript", "jackson-dataformat-hocon").version("2.1-SNAPSHOT")
        }

    }
}