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

val libs = extensions.getByType(org.gradle.accessors.dm.LibrariesForLibs::class)

dependencies {
    compileOnly(libs.wolfyutils)
    compileOnly(project(":core"))
    // Common Test libs
    testImplementation(libs.wolfyutils)
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

publishing {
    publications {
        create<MavenPublication>("lib") {
            from(components.getByName("java"))
            artifact(file("$rootDir/gradle.properties"))
        }
    }
}

paperweight.reobfArtifactConfiguration = io.papermc.paperweight.userdev.ReobfArtifactConfiguration.REOBF_PRODUCTION

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
        options.release.set(21)
    }
    reobfJar {}
}
