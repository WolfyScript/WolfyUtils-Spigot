package com.wolfyscript.docker

import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional

abstract class DockerRunExtension {

    @get:Input
    abstract val name: Property<String>

    @get:Input
    abstract val image: Property<String>

    @get:Input
    @get:Optional
    abstract val network: Property<String>

    @get:Input
    @get:Optional
    abstract val daemonize: Property<Boolean>

    @get:Input
    @get:Optional
    abstract val clean: Property<Boolean>

    @get:Input
    @get:Optional
    abstract val ignoreExitValue: Property<Boolean>

    @get:Input
    @get:Optional
    abstract val command: ListProperty<String>

    @get:Input
    @get:Optional
    abstract val env: MapProperty<String, String>

    abstract val ports: SetProperty<String>

    abstract val arguments: ListProperty<String>

    abstract val volumes: MapProperty<Any, String>

    fun arguments(vararg arguments: String) {
        this.arguments.set(listOf(elements = arguments))
    }

    fun ports(vararg ports: String) {
        this.ports.set(buildSet {
            for (port in ports) {
                val mapping = port.split(":", limit = 2)
                if (mapping.size == 1) {
                    isPortValid(mapping[0])
                    this.add("${mapping[0]}:${mapping[0]}")
                } else {
                    isPortValid(mapping[0])
                    isPortValid(mapping[1])
                    this.add("${mapping[0]}:${mapping[1]}")
                }
            }
        })
    }

    private fun isPortValid(port: String) {
        if (Integer.parseInt(port) !in 1..65536) {
            throw IllegalArgumentException("Port must be in the range [1,65536]")
        }
    }

    fun volumes(volumes: Map<Any, String>) {
        this.volumes.set(volumes.toMap())
    }

    fun env(env: Map<String, String>) {
        this.env.set(env.toMap())
    }

}