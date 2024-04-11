plugins {
    `java-library`
    `maven-publish`
    id("com.jfrog.artifactory")
}

repositories {
    mavenLocal()
    mavenCentral()
    maven {
        url = uri("https://maven.enginehub.org/repo/")
    }

    maven {
        url = uri("https://maven.wolfyscript.com/repository/public/")
    }

    maven {
        url = uri("https://repo.maven.apache.org/maven2/")
    }

    maven {
        url = uri("https://jitpack.io")
    }

    maven {
        url = uri("https://repo.citizensnpcs.co")
    }

    maven {
        url = uri("https://nexus.phoenixdevt.fr/repository/maven-public/")
    }
}

java {
    // Configure the java toolchain. This allows gradle to auto-provision JDK 17 on systems that only have JDK 8 installed for example.
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

group = "com.wolfyscript.wolfyutils.spigot"
version = "5.0-alpha.2-SNAPSHOT"
val apiVersion = "5.0-SNAPSHOT"

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    compileOnly("io.papermc.paper:paper-api:1.20.2-R0.1-SNAPSHOT")
    compileOnly("com.wolfyscript.wolfyutils:common:${version}")
    compileOnly("com.fasterxml.jackson.core:jackson-databind:2.16.1")
    compileOnly("org.jetbrains:annotations:23.0.0")
    compileOnly("com.mojang:authlib:1.5.21")
    compileOnly("io.netty:netty-all:4.1.85.Final")
    compileOnly("org.bstats:bstats-bukkit:3.0.0")
    compileOnly("de.tr7zw:item-nbt-api:2.11.3")
    compileOnly("de.tr7zw:nbt-data-api:2.11.3")
    // Common Test libs
    testImplementation("com.wolfyscript.wolfyutils:common:${version}")
}

java.sourceCompatibility = JavaVersion.VERSION_17

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}

tasks.withType<JavaCompile>() {
    options.encoding = "UTF-8"
}

tasks.withType<Javadoc>() {
    options.encoding = "UTF-8"
}
