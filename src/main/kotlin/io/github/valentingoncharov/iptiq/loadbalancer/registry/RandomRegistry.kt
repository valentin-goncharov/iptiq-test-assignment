package io.github.valentingoncharov.iptiq.loadbalancer.registry

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.withContext
import kotlin.random.Random

@OptIn(DelicateCoroutinesApi::class)
class RandomRegistry<T>: Registry<T> {

    private val registryContext = newSingleThreadContext("Registry-Context")

    private val registry = mutableListOf<T>()

    override suspend fun add(node: T) = withContext(registryContext) {
        registry.takeIf { node !in it }?.add(node) ?: false
    }

    override suspend fun remove(node: T) = withContext(registryContext) {
        registry.removeIf { node == it }
    }

    override suspend fun next() = withContext(registryContext) {
        if (registry.isEmpty()) {
            error("Load balancer registry is empty")
        }
        registry[nextIndex()]
    }

    override val size: Int
        get() = registry.size

    private fun nextIndex(): Int = Random.nextInt(registry.size)
}
