package io.github.valentingoncharov.iptiq.loadbalancer.registry

import kotlin.random.Random

class RandomRegistry<T>: AbstractRegistry<T>() {
    override val size: Int
        get() = super.size

    override fun nextIndex(): Int = Random.nextInt(registry.size)
}
