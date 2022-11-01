package io.github.valentingoncharov.iptiq.loadbalancer

import io.github.valentingoncharov.iptiq.healthcheck.HealthCheck
import io.github.valentingoncharov.iptiq.loadbalancer.registry.Registry
import io.github.valentingoncharov.iptiq.provider.IdProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.withContext
import java.util.concurrent.atomic.AtomicInteger

const val MAXIMUM_LOAD_BALANCER_CAPACITY = 10
const val PROVIDER_CAPACITY = 2

@OptIn(DelicateCoroutinesApi::class)
class LoadBalancer<T: IdProvider>(
    private val registry: Registry<T>,
    private val capacity: Int = MAXIMUM_LOAD_BALANCER_CAPACITY,
    private val healthCheck: HealthCheck<T> = HealthCheck(registry)

) {

    private val loadBalancerContext: CoroutineDispatcher = newSingleThreadContext("Load-Balancer-Context")

    private val registeredProviders = mutableMapOf<String, T>()

    private val activeRequests = AtomicInteger(0)

    suspend fun get(): String {
        try {
            if (activeRequests.incrementAndGet() > PROVIDER_CAPACITY * registry.size ) {
                error("Cluster capacity exceeded")
            }
            return registry.next().get()
        } finally {
            activeRequests.decrementAndGet()
        }
    }

    suspend fun register(provider: T) = withContext(loadBalancerContext) {
        registeredProviders.putIfAbsent(provider.id, provider)
        include(provider.id)
    }

    suspend fun include(providerId: String) = withContext(loadBalancerContext) {
        registeredProviders[providerId] ?. let {provider ->
            if (registry.size < capacity) {
                registry.add(provider).let {
                    healthCheck.start(provider)
                    it
                }
            } else {
                false
            }
        } ?: false
    }

    suspend fun exclude(providerId: String) = withContext(loadBalancerContext){
        registeredProviders[providerId] ?. let { provider ->
            registry.remove(provider).let {
                healthCheck.stop(provider)
                it
            }
        } ?: false
    }
}
