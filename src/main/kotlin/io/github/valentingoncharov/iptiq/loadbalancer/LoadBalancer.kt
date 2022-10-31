package io.github.valentingoncharov.iptiq.loadbalancer

import io.github.valentingoncharov.iptiq.loadbalancer.registry.Registry
import io.github.valentingoncharov.iptiq.provider.Provider
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.withContext

const val MAXIMUM_LOAD_BALANCER_CAPACITY = 10

@OptIn(DelicateCoroutinesApi::class)
class LoadBalancer(
    private val registry: Registry<Provider>,
    private val capacity: Int = MAXIMUM_LOAD_BALANCER_CAPACITY
) {

    private val loadBalancerContext = newSingleThreadContext("Load-Balancer-Context")

    private val registeredProviders = mutableMapOf<String, Provider>()

    suspend fun get(): String = registry.next().get()

    suspend fun register(provider: Provider) = withContext(loadBalancerContext) {
        registeredProviders.putIfAbsent(provider.id, provider)
        include(provider.id)
    }

    suspend fun include(providerId: String) = withContext(loadBalancerContext) {
        registeredProviders[providerId] ?. let {provider ->
            if (registry.size < capacity) {
                registry.add(provider)
            } else {
                false
            }
        } ?: false
    }

    suspend fun exclude(providerId: String) = withContext(loadBalancerContext){
        registeredProviders[providerId] ?. let { provider ->
            registry.remove(provider)
        } ?: false
    }
}
