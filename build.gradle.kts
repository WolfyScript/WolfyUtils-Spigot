import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import com.wolfyscript.docker.DockerRunTask
import com.wolfyscript.docker.DockerStopTask
import java.util.*

/*
 * This file was generated by the Gradle 'init' task.
 */

plugins {
    id("com.wolfyscript.wolfyutils.spigot.java-conventions")
    id("com.github.johnrengelman.shadow") version ("8.1.1")
    id("docker-run")
}

description = "wolfyutils-spigot"

dependencies {
    api(apis.wolfyutils)
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

// Default values for test server docker containers
dockerRun {
    name.set("test_minecraft_server")
    image.set("itzg/minecraft-server")
    daemonize.set(true)
    clean.set(true) // create a temporary server container

    ports("25565:25565")

    val serverEnv = mutableMapOf<String, String>()
    serverEnv["TYPE"] = "SPIGOT"
    serverEnv["VERSION"] = "1.20.1"
    serverEnv["GUI"] = "FALSE"
    serverEnv["EULA"] = "TRUE"
    serverEnv["MEMORY"] = "2G"
    serverEnv["USE_AIKAR_FLAGS"] = "TRUE"
    env(serverEnv)
}

val servers = buildMap {
    this["SPIGOT"] = listOf("1.20.1", "1.19.4", "1.18.2", "1.17.1")
    this["PAPER"] = listOf("1.20.1", "1.19.4")
}

var port = 25565
for (entry in servers.entries) {
    val server = entry.key
    for (version in entry.value) {
        val versionName = version.replace('.', '_')
        val serverName = "${server}_${versionName}";
        val serverPath = "./test_servers/${server.lowercase(Locale.ROOT)}_${versionName}"

        val copyTask = task<Copy>("${serverName}_copy") {
            dependsOn("shadowJar")

            println("Copy Jar to server: $serverPath/plugins")
            from(layout.buildDirectory.dir("libs/wolfyutils-spigot-${project.version}.jar"))
            into("$serverPath/plugins")
        }

        val stopTask = task<DockerStopTask>("${serverName}_stop") {
            applyExtension(project.dockerRun)
            name.set("${name.get()}_$serverName")
        }

        task<DockerRunTask>("${serverName}_run") {
            dependsOn(copyTask)
            dependsOn(stopTask)

            applyExtension(project.dockerRun)
            name.set("${name.get()}_$serverName")

            println(serverPath)
            mkdir(serverPath)

            ports.set(listOf("${port}:25565"))

            val customEnv = env.get().toMutableMap()
            customEnv["VERSION"] = version
            env.set(customEnv)

            val customVolumes = volumes.get().toMutableMap()
            customVolumes[serverPath] = "/data"
            volumes.set(customVolumes)
        }

        port++
    }
}

tasks.named<ShadowJar>("shadowJar") {
    dependsOn(project(":nmsutil").tasks.named("shadowJar"))

    archiveClassifier.set("")

    dependencies {
        include(dependency(apis.wolfyutils.get().toString()))
        include(dependency(apis.dataformat.hocon.get().toString()))
        include(dependency("${libs.bstats.get().group}:.*"))
        include(dependency("${libs.nbtapi.api.get().group}:.*"))
        include(project(":core"))
        include(project(":plugin-compatibility"))
        include(project(":nmsutil"))
    }

    relocate("org.bstats", "com.wolfyscript.utilities.bukkit.metrics")

    relocate("de.tr7zw.changeme.nbtapi", "com.wolfyscript.lib.de.tr7zw.nbtapi")
    relocate("de.tr7zw", "com.wolfyscript.lib.de.tr7zw")
}

tasks.named("test") {
    dependsOn.add(tasks.named("shadowJar"))
}
