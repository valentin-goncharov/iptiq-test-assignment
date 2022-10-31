package io.github.valentingoncharov.iptiq.loadbalancer.registry

import io.github.valentingoncharov.iptiq.provider.Provider
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.withContext

@OptIn(DelicateCoroutinesApi::class)
class TestProviderRegistry: Registry<Provider> {

    private val registryContext = newSingleThreadContext("Test-Provider-Registry-Context")

    private var registry = mutableSetOf<Provider>()

    override suspend fun add(node: Provider) = withContext(registryContext) {
        registry.add(node)
    }

    override suspend fun remove(node: Provider) = withContext(registryContext) {
        registry.remove(node)
    }

    override suspend fun next() = withContext(registryContext) {
        registry.first()
    }

    override val size: Int
        get() = registry.size
}
