package io.github.valentingoncharov.iptiq.loadbalancer.registry

import kotlin.random.Random

class RandomRegistry<T>: AbstractRegistry<T>() {

    override fun nextIndex(): Int = Random.nextInt(registry.size)
}
