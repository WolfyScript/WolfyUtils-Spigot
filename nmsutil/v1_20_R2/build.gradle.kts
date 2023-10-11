description = "v1_20_R2"
private val mcVersion = "1.20.2"

plugins {
    id("com.wolfyscript.wolfyutils.spigot.java-conventions")
    id("io.github.patrick.remapper") version "1.4.0"
    id("com.wolfyscript.devtools.buildtools") version ("2.0-SNAPSHOT")
}

dependencies {
    compileOnly(group = "org.spigotmc", name = "spigot", version = "1.20.2-R0.1-SNAPSHOT", classifier = "remapped-mojang")
    compileOnly(project(":core"))
}

buildTools {
    parent?.ext?.let {
        buildToolsDir.set(file(it.get("buildToolsDir").toString()))
        buildToolsJar.set(file(it.get("buildToolsJar").toString()))
    }
    minecraftVersion.set(mcVersion)
}

tasks {
    remap {
        version.set(mcVersion)
        dependsOn("jar")
    }
    jar {
        finalizedBy("remap")
    }
}