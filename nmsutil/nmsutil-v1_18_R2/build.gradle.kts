description = "nmsutil-v1_18_R2"

plugins {
    id("com.wolfyscript.wolfyutils.spigot.java-conventions")
    id("io.github.patrick.remapper") version "1.4.0"
}

dependencies {
    compileOnly(group = "org.spigotmc", name = "spigot", version = "1.18.2-R0.1-SNAPSHOT", classifier = "remapped-mojang")
    compileOnly(project(":core"))
}

tasks {
    remap {
        version.set("1.18.2")
        dependsOn("jar")
    }
    jar {
        finalizedBy("remap")
    }
}
