/*
 * This file was generated by the Gradle 'init' task.
 */

plugins {
    id("com.wolfyscript.wolfyutils.spigot.java-conventions")
}

dependencies {
    compileOnly(group = "org.spigotmc", name = "spigot", version = "1.18.1-R0.1-SNAPSHOT", classifier = "remapped-mojang")
    compileOnly(project(":core"))
}

description = "nmsutil-v1_18_R1_P1"
