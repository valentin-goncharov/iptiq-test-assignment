package io.github.valentingoncharov.iptiq.loadbalancer

import io.github.valentingoncharov.iptiq.provider.Provider

const val MAXIMUM_LOAD_BALANCER_CAPACITY = 10
class LoadBalancer(
    private val capacity: Int = MAXIMUM_LOAD_BALANCER_CAPACITY
) {
    private val registeredProviders = mutableMapOf<String, Provider>()

    fun register(provider: Provider) = registeredProviders[provider.id]
        ?. let { false }
        ?: let {
            if (registeredProviders.size < capacity) {
                registeredProviders[provider.id] = provider
                true
            } else {
                false
            }
        }
}
