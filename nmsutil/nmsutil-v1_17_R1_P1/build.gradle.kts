description = "nmsutil-v1_17_R1_P1"

plugins {
    id("com.wolfyscript.wolfyutils.spigot.java-conventions")
    id("io.github.patrick.remapper") version "1.4.0"
}

dependencies {
    compileOnly(group = "org.spigotmc", name = "spigot", version = "1.17.1-R0.1-SNAPSHOT", classifier = "remapped-mojang")
    compileOnly(project(":core"))
}

tasks {
    remap {
        version.set("1.17.1")
        dependsOn("jar")
    }
    jar {
        finalizedBy("remap")
    }
}
