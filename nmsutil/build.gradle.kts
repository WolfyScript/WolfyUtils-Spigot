import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("com.wolfyscript.wolfyutils.spigot.java-conventions")
    id("com.github.johnrengelman.shadow") version ("8.1.1")
}

description = "nmsutil"

// Specify directory in home directory as buildtools source
ext.set("buildToolsDir", "${System.getProperty("user.home")}${File.separator}minecraft${File.separator}buildtools")
ext.set("buildToolsJar", "${System.getProperty("user.home")}${File.separator}minecraft${File.separator}buildtools${File.separator}BuildTools.jar")

dependencies {
    subprojects.forEach {
        implementation(project(path = it.path, configuration = "reobf")) // We need to use the reobf sources to shade the obfuscated classes
    }

    compileOnly("com.google.inject:guice:5.1.0")
    compileOnly("org.reflections:reflections:0.10.2")
    compileOnly("org.javassist:javassist:3.29.2-GA")
    compileOnly("net.kyori:adventure-api:4.14.0")
    compileOnly("net.kyori:adventure-platform-bukkit:4.1.2")
    compileOnly("net.kyori:adventure-text-minimessage:4.14.0")
}

tasks {
    named<ShadowJar>("shadowJar") {
        mergeServiceFiles()
        archiveClassifier.set("")

        // Need to run this shadowJar after the subprojects have been obfuscated
        subprojects.forEach { subProject ->
            dependsOn(subProject.tasks.build)
        }

        dependencies {
            subprojects.forEach {
                include(project(it.path))
            }
        }
    }
}