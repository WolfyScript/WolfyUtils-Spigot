import org.jfrog.gradle.plugin.artifactory.task.ArtifactoryTask

plugins {
    `java-library`
    `maven-publish`
    id("com.jfrog.artifactory")
    id("io.papermc.paperweight.userdev")
}

repositories {
    mavenLocal()
    mavenCentral()

    maven(url = "https://artifacts.wolfyscript.com/artifactory/gradle-dev")

    maven(url = "https://libraries.minecraft.net/")
}

val apiVersion = "4.16.1-SNAPSHOT"

dependencies {
    compileOnly("com.wolfyscript.wolfyutils:wolfyutilities:${apiVersion}")
    compileOnly(project(":core"))
    // Common Test libs
    testImplementation("com.wolfyscript.wolfyutils:wolfyutilities:${apiVersion}")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

publishing {
    publications {
        create<MavenPublication>("lib") {
            from(components.getByName("java"))
            artifact(file("$rootDir/gradle.properties"))
        }
    }
}

tasks {
    withType<ArtifactoryTask> {
        skip = true
    }
    jar {
        enabled = true
    }
    assemble {
        dependsOn(reobfJar)
    }
    javadoc {
        options.encoding = Charsets.UTF_8.name()
    }
    compileJava {
        options.release.set(17)
    }
    reobfJar {}
}
