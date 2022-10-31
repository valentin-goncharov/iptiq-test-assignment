package io.github.valentingoncharov.iptiq.loadbalancer.registry

import kotlinx.coroutines.withContext

class RoundRobinRegistry<T>: AbstractRegistry<T>() {

    private var counter = -1;

    override suspend fun remove(node: T) = withContext(registryContext) {
        val index = registry.indexOf(node)
        if (index > -1) {
            if (
                index <= counter) {
                counter--
            }
            registry.removeAt(index)
            true
        } else {
            false
        }
    }

    override fun nextIndex():Int {
        counter = (counter + 1) % registry.size
        return counter
    }
}
