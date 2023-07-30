import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

/*
 * This file was generated by the Gradle 'init' task.
 */

plugins {
    id("com.wolfyscript.wolfyutils.spigot.java-conventions")
    id("com.github.johnrengelman.shadow") version("8.1.1")
}

description = "wolfyutils-spigot"

dependencies {
    api(apis.wolfyutils)
    implementation(project(":core"))
    implementation(project(":plugin-compatibility-artifact"))
    implementation(project(":nmsutil-artifact"))
    api(libs.guice)
    api(libs.reflections)
    api(libs.javassist)
    api(libs.adventure.api)
    api(libs.adventure.minimessage)
    api(libs.adventure.platform.bukkit)

    testImplementation(project(":core"))
    testImplementation(testLibs.junit.jupiter)
    testImplementation(testLibs.mockito)
    testImplementation(testLibs.mockbukkit)
}

tasks.named<ShadowJar>("shadowJar") {

    dependencies {
        include(dependency("com.wolfyscript.wolfyutils:wolfyutilities:5.0-SNAPSHOT"))
        include(project(":core"))
        include(project(":plugin-compatibility-artifact"))
        include(project(":nmsutil-artifact"))
    }
}