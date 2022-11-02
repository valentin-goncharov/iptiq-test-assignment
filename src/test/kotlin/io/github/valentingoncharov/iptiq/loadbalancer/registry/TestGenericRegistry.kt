package io.github.valentingoncharov.iptiq.loadbalancer.registry

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.withContext

@OptIn(DelicateCoroutinesApi::class)
class TestGenericRegistry<T>: Registry<T> {

    private val registryContext = newSingleThreadContext("Test-Generic-Registry-Context")

    private var registry = mutableSetOf<T>()

    override suspend fun add(node: T) = withContext(registryContext) {
        registry.add(node)
    }

    override suspend fun remove(node: T) = withContext(registryContext) {
        registry.remove(node)
    }

    override suspend fun next() = withContext(registryContext) {
        registry.first()
    }

    override val size: Int
        get() = registry.size
}
