package com.wolfyscript.docker

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.*

class DockerRunPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        val extension = target.extensions.create<DockerRunExtension>("DockerRunExtension")
        target.extensions.add("dockerRun", extension)

        target.tasks.register<DockerRunTask>("dockerRun") {
            applyExtension(extension)
        }

        target.tasks.register("dockerStatus") {


        }

        target.tasks.register<DockerStopTask>("dockerStop") {
            applyExtension(extension)
        }

    }

}
