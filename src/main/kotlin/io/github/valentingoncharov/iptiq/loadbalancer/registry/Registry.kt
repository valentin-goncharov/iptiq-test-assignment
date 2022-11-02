package io.github.valentingoncharov.iptiq.loadbalancer.registry

interface Registry<T> {
    val size: Int

    suspend fun add(node :T): Boolean
    suspend fun remove(node :T): Boolean
    suspend fun next(): T
}
