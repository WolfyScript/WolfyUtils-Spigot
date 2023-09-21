import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("com.wolfyscript.wolfyutils.spigot.java-conventions")
    id("com.github.johnrengelman.shadow") version("8.1.1")
}

description = "nmsutil"

dependencies {
    subprojects.forEach {
        api(it)
    }

    compileOnly("com.google.inject:guice:5.1.0")
    compileOnly("org.reflections:reflections:0.10.2")
    compileOnly("org.javassist:javassist:3.29.2-GA")
    compileOnly("net.kyori:adventure-api:4.14.0")
    compileOnly("net.kyori:adventure-platform-bukkit:4.1.2")
    compileOnly("net.kyori:adventure-text-minimessage:4.14.0")
}

tasks.named<ShadowJar>("shadowJar") {
    subprojects.forEach {
        val remapTask = it.tasks.findByName("remap")
        if (remapTask != null) {
            dependsOn(remapTask)
        }
    }

    archiveClassifier.set("")

    dependencies {
        subprojects.forEach {
            include(project(it.path))
        }
    }
}