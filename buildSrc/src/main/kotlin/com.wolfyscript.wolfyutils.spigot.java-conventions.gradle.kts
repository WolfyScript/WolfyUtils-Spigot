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
    maven(url = "https://mvn.lumine.io/repository/maven-public/")
    maven(url = "https://maven.devs.beer/")
    maven(url = "https://repo.auxilor.io/repository/maven-public/")
}

java {
    // Configure the java toolchain. This allows gradle to auto-provision JDK 17 on systems that only have JDK 8 installed for example.
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

val libs = extensions.getByType(org.gradle.accessors.dm.LibrariesForLibs::class)

dependencies {
    compileOnly(libs.io.papermc.paper)
    compileOnly(libs.wolfyutils)
    compileOnly(libs.jackson.dataformat.hocon)
    compileOnly(libs.jetbrains.annotations)
    compileOnly(libs.mojang.authlib)
    compileOnly(libs.netty)
    compileOnly(libs.bstats)
    compileOnly(libs.nbtapi)
    // Common Test libs
    testImplementation(libs.wolfyutils)
}

publishing {
    publications {
        create<MavenPublication>("lib") {
            from(components.getByName("java"))
            artifact(file("$rootDir/gradle.properties"))
        }
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.release.set(21)
}

tasks.withType<Javadoc>() {
    options.encoding = "UTF-8"
}