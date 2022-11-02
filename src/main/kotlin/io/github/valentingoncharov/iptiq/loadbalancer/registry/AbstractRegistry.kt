package io.github.valentingoncharov.iptiq.loadbalancer.registry

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.withContext

@OptIn(DelicateCoroutinesApi::class)
abstract class AbstractRegistry<T> : Registry<T> {

    protected val registryContext = newSingleThreadContext("Registry-Context")

    protected val registry = mutableListOf<T>()

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

    protected abstract fun nextIndex(): Int
}
