rootProject.name = "wolfyutils-spigot"

pluginManagement {
    repositories {
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()
        maven("https://artifacts.wolfyscript.com/artifactory/gradle-dev")
    }
}

sequenceOf(
    "core",
    "plugin-compatibility"
).forEach {
    include(":${it}")
    project(":${it}").projectDir = file(it.replace(":", "/"))
}
