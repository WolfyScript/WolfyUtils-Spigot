import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("com.wolfyscript.wolfyutils.spigot.java-conventions")
    id("io.github.goooler.shadow") version "8.1.7"
    id("com.wolfyscript.devtools.docker.run") version "2.0-SNAPSHOT"
    id("com.wolfyscript.devtools.docker.minecraft_servers") version "2.0-SNAPSHOT"
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

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

tasks.named<ProcessResources>("processResources") {
    expand(project.properties)
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

val debugPort: String = "5006"

minecraftDockerRun {
//    clean.set(false)
    val customEnv = env.get().toMutableMap()
    customEnv["MEMORY"] = "2G"
    customEnv["JVM_OPTS"] = "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:${debugPort}"
    customEnv["FORCE_REDOWNLOAD"] = "false"
    env.set(customEnv)
    arguments("--cpus", "2", "-it") // Constrain to only use 2 cpus, and allow for console interactivity with 'docker attach'
}

minecraftServers {
    serversDir.set(file("${System.getProperty("user.home")}${File.separator}minecraft${File.separator}test_servers_v4"))
    libName.set("${project.name}-${version}.jar")
    val debugPortMapping = "${debugPort}:${debugPort}"
    servers {
        register("spigot_1_17") {
            version.set("1.17.1")
            imageVersion.set("java17")
            type.set("SPIGOT")
            ports.set(setOf(debugPortMapping, "25565:25565"))
        }
        register("spigot_1_18") {
            version.set("1.18.2")
            imageVersion.set("java17")
            type.set("SPIGOT")
            ports.set(setOf(debugPortMapping, "25566:25565"))
        }
        register("spigot_1_19") {
            version.set("1.19.4")
            imageVersion.set("java17")
            type.set("SPIGOT")
            ports.set(setOf(debugPortMapping, "25567:25565"))
        }
        register("spigot_1_20") {
            version.set("1.20.4")
            imageVersion.set("java17")
            type.set("SPIGOT")
            ports.set(setOf(debugPortMapping, "25568:25565"))
        }
        register("spigot_1_20_6") {
            version.set("1.20.6")
            type.set("SPIGOT")
            imageVersion.set("java21")
            ports.set(setOf(debugPortMapping, "25569:25565"))
        }
        // Paper test servers
        register("paper_1_20") {
            version.set("1.20.6")
            type.set("PAPER")
            imageVersion.set("java21")
            ports.set(setOf("5007:5007", "25569:25565"))
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
    mergeServiceFiles()

    manifest {
        attributes(Pair("paperweight-mappings-namespace", "spigot"))
    }

    archiveClassifier.set("")

    dependencies {
        include(dependency(apis.wolfyutils.get().toString()))
        include(dependency(apis.dataformat.hocon.get().toString()))
        include(dependency("${libs.jackson.get().group}:.*"))
        include(dependency("${libs.bstats.get().group}:.*"))
        include(dependency("${libs.nbtapi.api.get().group}:.*"))
        include(dependency("${libs.reflections.get().group}:.*"))
        include(dependency("${libs.javassist.get().group}:.*"))
        include(dependency("${libs.adventure.api.get().group}:.*"))
        include(dependency("${libs.adventure.platform.bukkit.get().group}:.*"))
        include(dependency("${libs.adventure.minimessage.get().group}:.*"))
        include(dependency("${libs.typesafe.config.get().group}:.*"))
        include(project(":core"))
        include(project(":plugin-compatibility"))
        include(project(":nmsutil"))
    }

    // Always required to be shaded and relocated!
    relocate("org.bstats", "com.wolfyscript.utilities.bukkit.metrics")

    // Dependencies (pre spigot plugin.yml dependency update) required to be shaded! To be removed in v5!
    relocate("com.typesafe", "com.wolfyscript.lib.com.typesafe")
    relocate("de.tr7zw.changeme.nbtapi", "com.wolfyscript.lib.nbt.nbtapi")

    // Still using me.wolfyscript.lib package! To be changed/removed in v5!
    relocate("org.reflections", "me.wolfyscript.lib.org.reflections")
    relocate("javassist", "me.wolfyscript.lib.javassist")
    relocate("com.fasterxml.jackson", "me.wolfyscript.lib.com.fasterxml.jackson")
    relocate("net.kyori", "me.wolfyscript.lib.net.kyori")
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