import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("com.wolfyscript.wolfyutils.spigot.java-conventions")
    id("com.github.johnrengelman.shadow") version ("8.1.1")
    id("com.wolfyscript.devtools.docker.run") version ("2.0-SNAPSHOT")
    id("com.wolfyscript.devtools.docker.minecraft_servers") version ("2.0-SNAPSHOT")
    kotlin("jvm")
}

description = "wolfyutils-spigot"

dependencies {
    api(apis.wolfyutils.api)
    implementation(apis.wolfyutils.common)
    implementation(project(":core"))
    implementation(project(":plugin-compatibility"))
    implementation(project(":nmsutil"))
    api(libs.bstats)
    api(libs.guice)
    api(libs.reflections)
    api(libs.javassist)
    api(libs.adventure.api)
    api(libs.adventure.minimessage)
    api(libs.adventure.platform.bukkit)
    api(libs.nbtapi.api)
    api(libs.nbtapi.data)

    testImplementation(project(":core"))
    testImplementation(testLibs.junit.jupiter)
    testImplementation(testLibs.mockito)
    testImplementation(testLibs.mockbukkit)
}

tasks.named<ProcessResources>("processResources") {
    expand(project.properties)
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

val debugPort: String = "5006"

minecraftDockerRun {
    val customEnv = env.get().toMutableMap()
    customEnv["MEMORY"] = "2G"
    customEnv["JVM_OPTS"] = "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:${debugPort}"
    customEnv["FORCE_REDOWNLOAD"] = "false"
    env.set(customEnv)
    arguments("--cpus", "2", "-it") // Constrain to only use 2 cpus, and allow for console interactivity with 'docker attach'
}

minecraftServers {
    serversDir.set(file("${System.getProperty("user.home")}${File.separator}minecraft${File.separator}test_servers_v5"))
    libName.set("${project.name}-${version}.jar")
    val debugPortMapping = "${debugPort}:${debugPort}"
    servers {
        register("spigot_1_17") {
            version.set("1.17.1")
            type.set("SPIGOT")
            ports.set(setOf(debugPortMapping, "25565:25565"))
        }
        register("spigot_1_18") {
            version.set("1.18.2")
            type.set("SPIGOT")
            ports.set(setOf(debugPortMapping, "25566:25565"))
        }
        register("spigot_1_19") {
            version.set("1.19.4")
            type.set("SPIGOT")
            ports.set(setOf(debugPortMapping, "25567:25565"))
        }
        register("spigot_1_20") {
            version.set("1.20.4")
            type.set("SPIGOT")
            extraEnv.put("BUILD_FROM_SOURCE", "true") // 1.20.3 not available as download yet
            ports.set(setOf(debugPortMapping, "25568:25565"))
        }
        // Paper test servers
        register("paper_1_20") {
            version.set("1.20.2")
            type.set("PAPER")
            ports.set(setOf(debugPortMapping, "25569:25565"))
        }
        register("paper_1_19") {
            version.set("1.19.4")
            type.set("PAPER")
            ports.set(setOf(debugPortMapping, "25570:25565"))
        }
    }
}

tasks.named<ShadowJar>("shadowJar") {
    dependsOn(project(":nmsutil").tasks.named("shadowJar"))
    dependsOn(project(":core").tasks.named("shadowJar"))
    mustRunAfter("jar")

    archiveClassifier.set("")

    include("**")

    dependencies {
        include(dependency(apis.wolfyutils.common.get().toString()))
        include(dependency(apis.wolfyutils.api.get().toString()))
        include(dependency(apis.dataformat.hocon.get().toString()))
        include(dependency("${libs.bstats.get().group}:.*"))
        include(dependency("${libs.nbtapi.api.get().group}:.*"))
        include(project(":core"))
        include(project(":plugin-compatibility"))
        include(project(":nmsutil"))
    }

    // Always required to be shaded and relocated!
    relocate("org.bstats", "com.wolfyscript.utilities.bukkit.metrics")

    relocate("de.tr7zw.changeme.nbtapi", "com.wolfyscript.lib.de.tr7zw.nbtapi")
}

tasks.named("test") {
    dependsOn.add(tasks.named("shadowJar"))
}

artifactory {

    publish {
        contextUrl = "https://artifacts.wolfyscript.com/artifactory"
        repository {
            repoKey = "gradle-dev-local"
            username = project.properties["wolfyRepoPublishUsername"].toString()
            password = project.properties["wolfyRepoPublishToken"].toString()
        }
        defaults {
            publications("lib")
            setPublishArtifacts(true)
            setPublishPom(true)
            isPublishBuildInfo = false
        }
    }

}