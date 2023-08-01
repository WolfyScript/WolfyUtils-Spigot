package com.wolfyscript.docker

import org.gradle.api.DefaultTask
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import org.gradle.internal.logging.text.StyledTextOutput
import org.gradle.internal.logging.text.StyledTextOutputFactory
import org.gradle.kotlin.dsl.listProperty
import org.gradle.kotlin.dsl.mapProperty
import org.gradle.kotlin.dsl.property
import org.gradle.kotlin.dsl.setProperty
import org.gradle.kotlin.dsl.support.serviceOf

abstract class DockerRunTask : DefaultTask() {

    @get:Input
    val name: Property<String> = project.objects.property<String>()

    @get:Input
    val image: Property<String> = project.objects.property<String>()

    @get:Input
    @get:Optional
    val network: Property<String> = project.objects.property<String>()

    @get:Input
    val daemonize: Property<Boolean> = project.objects.property<Boolean>().convention(true)

    @get:Input
    val clean: Property<Boolean> = project.objects.property<Boolean>().convention(false)

    @get:Input
    val ignoreExitValue: Property<Boolean> = project.objects.property<Boolean>().convention(true)

    @get:Input
    @get:Optional
    val command: ListProperty<String> = project.objects.listProperty<String>()

    @get:Input
    @get:Optional
    val ports: SetProperty<String> = project.objects.setProperty<String>()

    @get:Input
    @get:Optional
    val env: MapProperty<String, String> = project.objects.mapProperty<String, String>()

    @get:Input
    @get:Optional
    val arguments: ListProperty<String> = project.objects.listProperty<String>()

    @get:Input
    @get:Optional
    val volumes: MapProperty<Any, String> = project.objects.mapProperty()

    private fun isPortValid(port: String) {
        if (Integer.parseInt(port) !in 1..65536) {
            throw IllegalArgumentException("Port must be in the range [1,65536]")
        }
    }

    fun applyExtension(extension: DockerRunExtension) {
        this.name.set(extension.name)
        this.image.set(extension.image)
        if (extension.network.isPresent) network.set(extension.network)
        if (extension.command.isPresent) command.set(extension.command)
        if (extension.daemonize.isPresent) daemonize.set(extension.daemonize)
        if (extension.clean.isPresent) clean.set(extension.clean)
        if (extension.ignoreExitValue.isPresent) ignoreExitValue.set(extension.ignoreExitValue)
        if (extension.ports.isPresent) ports.set(extension.ports)
        if (extension.env.isPresent) env.set(extension.env)
        if (extension.arguments.isPresent) arguments.set(extension.arguments)
        if (extension.volumes.isPresent) volumes.set(extension.volumes)
    }

    @TaskAction
    fun runDockerContainer() {
        val args = mutableListOf("docker", "run")
        if (daemonize.getOrElse(true)) {
            args.add("-d")
        }
        if (clean.getOrElse(false)) {
            args.add("--rm")
        } else {
            // TODO: finalizedBy("")
        }
        if (network.isPresent) {
            args.add("--network")
            args.add(network.get())
        }
        for (port in ports.get()) {
            //isPortValid(port)
            args.add("-p")
            args.add(port)
        }
        for (volume in volumes.get().entries) {
            val localFile = project.file(volume.key)

            if (!localFile.exists()) {
                val o = project.serviceOf<StyledTextOutputFactory>().create("docker-run")
                o.withStyle(StyledTextOutput.Style.Error)
                    .println("ERROR: Local folder $localFile doesn't exist. Mounted volume will not be visible to container")
                throw IllegalStateException("Local folder $localFile doesn't exist.")
            }

            args.add("-v")
            args.add("${localFile.absolutePath}:${volume.value}")
        }
        args.addAll(env.get().flatMap {
            listOf("-e", "${it.key}=${it.value}")
        })
        args.add("--name")
        args.add(name.get())
        for (arg in arguments.get()) {
            args.add(arg)
        }
        args.add(image.get())
        for (cmd in command.get()) {
            args.add(cmd)
        }

        println(args)
        project.exec {
            commandLine(args)
        }
    }

}