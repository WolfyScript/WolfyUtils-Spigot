plugins {
    `java-library`
    `maven-publish`
    id("com.jfrog.artifactory")
}

repositories {
    mavenLocal()
    mavenCentral()

    maven(url = "https://artifacts.wolfyscript.com/artifactory/gradle-dev")

    maven(url = "https://repo.codemc.io/repository/maven-public/")
    maven(url = "https://maven.enginehub.org/repo/")
    maven(url = "https://repo.maven.apache.org/maven2/")
    maven(url = "https://jitpack.io")
    maven(url = "https://repo.citizensnpcs.co")
    maven(url = "https://nexus.phoenixdevt.fr/repository/maven-public/")
    maven(url = "https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven(url = "https://libraries.minecraft.net/")
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
    publications {
        create<MavenPublication>("lib") {
            from(components.getByName("java"))
            artifact(file("$rootDir/gradle.properties"))
        }
    }
}

tasks.withType<JavaCompile>() {
    options.encoding = "UTF-8"
}

tasks.withType<Javadoc>() {
    options.encoding = "UTF-8"
}
