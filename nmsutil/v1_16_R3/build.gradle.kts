description = "v1_16_R3"

plugins {
    id("com.wolfyscript.wolfyutils.spigot.java-conventions")
}

dependencies {
    compileOnly(group = "org.spigotmc", name = "spigot", version = "1.16.4-R0.1-SNAPSHOT")
    compileOnly(project(":core"))
}

tasks {
}