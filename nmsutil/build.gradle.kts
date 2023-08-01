import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("com.wolfyscript.wolfyutils.spigot.java-conventions")
    id("com.github.johnrengelman.shadow") version("8.1.1")
}

description = "nmsutil"

dependencies {
    api(project(":nmsutil-v1_17_R1"))
    api(project(":nmsutil-v1_17_R1_P1"))
    api(project(":nmsutil-v1_18_R1"))
    api(project(":nmsutil-v1_18_R1_P1"))
    api(project(":nmsutil-v1_18_R2"))
    api(project(":nmsutil-v1_19_R1"))
    api(project(":nmsutil-v1_19_R2"))
    api(project(":nmsutil-v1_19_R3"))
    api(project(":nmsutil-v1_20_R1"))
    compileOnly("com.google.inject:guice:5.1.0")
    compileOnly("org.reflections:reflections:0.10.2")
    compileOnly("org.javassist:javassist:3.29.2-GA")
    compileOnly("net.kyori:adventure-api:4.14.0")
    compileOnly("net.kyori:adventure-platform-bukkit:4.1.2")
    compileOnly("net.kyori:adventure-text-minimessage:4.14.0")
}


tasks.named<ShadowJar>("shadowJar") {
    dependsOn(project(":nmsutil-v1_17_R1").tasks.named("remap"))
    dependsOn(project(":nmsutil-v1_17_R1_P1").tasks.named("remap"))
    dependsOn(project(":nmsutil-v1_18_R1").tasks.named("remap"))
    dependsOn(project(":nmsutil-v1_18_R1_P1").tasks.named("remap"))
    dependsOn(project(":nmsutil-v1_18_R2").tasks.named("remap"))
    dependsOn(project(":nmsutil-v1_19_R1").tasks.named("remap"))
    dependsOn(project(":nmsutil-v1_19_R2").tasks.named("remap"))
    dependsOn(project(":nmsutil-v1_19_R3").tasks.named("remap"))
    dependsOn(project(":nmsutil-v1_20_R1").tasks.named("remap"))
    dependsOn(project(":nmsutil-v1_20_R1").tasks.named("remap"))

    archiveClassifier.set("")

    dependencies {
        include(project(":nmsutil-v1_17_R1"))
        include(project(":nmsutil-v1_17_R1_P1"))
        include(project(":nmsutil-v1_18_R1"))
        include(project(":nmsutil-v1_18_R1_P1"))
        include(project(":nmsutil-v1_18_R2"))
        include(project(":nmsutil-v1_19_R1"))
        include(project(":nmsutil-v1_19_R2"))
        include(project(":nmsutil-v1_19_R3"))
        include(project(":nmsutil-v1_20_R1"))
    }
}
