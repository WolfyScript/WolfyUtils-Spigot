import com.wolfyscript.devtools.buildtools.BuildToolsInstallTask

description = "v1_16_R3"
private val mcVersion = "1.16.5"

plugins {
    id("com.wolfyscript.wolfyutils.spigot.java-conventions")
    id("com.wolfyscript.devtools.buildtools") version ("2.0-SNAPSHOT")
}

dependencies {
    compileOnly(group = "org.spigotmc", name = "spigot", version = "1.16.4-R0.1-SNAPSHOT")
    compileOnly(project(":core"))
}

buildTools {
    parent?.ext?.let {
        buildToolsDir.set(file(it.get("buildToolsDir").toString()))
        buildToolsJar.set(file(it.get("buildToolsJar").toString()))
    }
    minecraftVersion.set(mcVersion)
}

tasks.named<BuildToolsInstallTask>("prepareNMS") {
    javaLauncher.set(javaToolchains.launcherFor {
        languageVersion.set(JavaLanguageVersion.of(16))
    })
}