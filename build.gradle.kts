import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("com.wolfyscript.wolfyutils.spigot.java-conventions")
    alias(libs.plugins.goooler.shadow)
    alias(libs.plugins.devtools.docker.run)
    alias(libs.plugins.devtools.docker.minecraft)
    alias(libs.plugins.modrinth.minotaur)
}

description = "wolfyutils-spigot"

dependencies {
    api(libs.wolfyutils)
    implementation(project(":core"))
    implementation(project(":plugin-compatibility"))
    api(libs.bstats)
    api(libs.inject.guice)
    api(libs.reflections)
    api(libs.javassist)
    api(libs.adventure.api)
    api(libs.adventure.minimessage)
    api(libs.adventure.platform.bukkit)
    api(libs.nbtapi)

    testImplementation(project(":core"))
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
        register("spigot_1_21") {
            version.set("1.21.4")
            type.set("SPIGOT")
            extraEnv.put("BUILD_FROM_SOURCE", "true")
            imageVersion.set("java21-graalvm") // graalvm contains the jdk required to build from source
            ports.set(setOf(debugPortMapping, "25569:25565"))
        }
        // Paper test servers
        register("paper_1_21") {
            version.set("1.21.4")
            type.set("PAPER")
            imageVersion.set("java21")
            ports.set(setOf(debugPortMapping, "25569:25565"))
        }
    }
}

tasks.named<ShadowJar>("shadowJar") {
    dependsOn(project(":core").tasks.named("shadowJar"))
    mustRunAfter("jar")
    mergeServiceFiles()

    manifest {
        attributes(Pair("paperweight-mappings-namespace", "spigot"))
    }

    archiveClassifier.set("")

    dependencies {
        include(dependency(libs.wolfyutils.get().toString()))
        include(dependency(libs.jackson.dataformat.hocon.get().toString()))
        include(dependency("${libs.jackson.databind.get().group}:.*"))
        include(dependency("${libs.bstats.get().group}:.*"))
        include(dependency("${libs.reflections.get().group}:.*"))
        include(dependency("${libs.javassist.get().group}:.*"))
        include(dependency("${libs.adventure.api.get().group}:.*"))
        include(dependency("${libs.adventure.platform.bukkit.get().group}:.*"))
        include(dependency("${libs.adventure.minimessage.get().group}:.*"))
        include(dependency("${libs.typesafe.config.get().group}:.*"))
        include(project(":core"))
        include(project(":plugin-compatibility"))
    }

    // Always required to be shaded and relocated!
    relocate("org.bstats", "com.wolfyscript.utilities.bukkit.metrics")

    // Dependencies (pre spigot plugin.yml dependency update) required to be shaded! To be removed in v5!
    relocate("com.typesafe", "com.wolfyscript.lib.com.typesafe")

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
            username = System.getenv("ARTIFACTORY_USERNAME")
            password = System.getenv("ARTIFACTORY_TOKEN")
        }
        defaults {
            publications("lib")
            setPublishArtifacts(true)
            setPublishPom(true)
            isPublishBuildInfo = false
        }
    }

}

modrinth {
    token.set(System.getenv("MODRINTH_TOKEN")) // Remember to have the MODRINTH_TOKEN environment variable set or else this will fail - just make sure it stays private!
    projectId.set("wolfyutils") // This can be the project ID or the slug. Either will work!
    versionNumber.set(project.version.toString()) // You don't need to set this manually. Will fail if Modrinth has this version already
    versionType.set("release") // TODO: Automatically determine this from the version
    uploadFile.set(tasks.shadowJar) // Use the shadowed jar !!
    changelog.set(System.getenv("CHANGELOG"))
    gameVersions.addAll("1.21.4") // Must be an array, even with only one version
    loaders.addAll("bukkit", "spigot", "paper", "purpur") // Must also be an array - no need to specify this if you're using Loom or ForgeGradle
}
