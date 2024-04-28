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
    reobfJar {
        // This is an example of how you might change the output location for reobfJar. It's recommended not to do this
        // for a variety of reasons, however it's asked frequently enough that an example of how to do it is included here.
//        outputJar = layout.buildDirectory.file("libs/PaperweightTestPlugin-${project.version}.jar")
    }
}
