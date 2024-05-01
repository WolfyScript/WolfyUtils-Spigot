plugins {
    // Support convention plugins written in Kotlin. Convention plugins are build scripts in 'src/main' that automatically become available as plugins in the main build.
    `kotlin-dsl`
}

repositories {
    // Use the plugin portal to apply community plugins in convention plugins.
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    implementation("org.jfrog.buildinfo:build-info-extractor-gradle:5.2.0")
    implementation(group = "io.papermc.paperweight", name = "paperweight-userdev", version = "1.6.2")
}

gradlePlugin {

}
