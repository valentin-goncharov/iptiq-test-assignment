package io.github.valentingoncharov.iptiq.loadbalancer

import io.github.valentingoncharov.iptiq.loadbalancer.registry.RandomRegistry
import io.github.valentingoncharov.iptiq.provider.Provider
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.newFixedThreadPoolContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import org.junit.jupiter.api.Test
import java.util.UUID
import kotlin.random.Random

@OptIn(DelicateCoroutinesApi::class)
class LoadBalancerIT {

    @Test
    fun `multi threading test`() {

        val pool = newFixedThreadPoolContext(30, "Test-pool")

        val balancer = LoadBalancer(RandomRegistry<PrintableProvider>())
        runBlocking(pool) {
            repeat(10) {
                balancer.register(PrintableProvider())
            }

            repeat(100) {
                launch {
                    withTimeoutOrNull(30000L) {
                        val id = UUID.randomUUID().toString()
                        while(true){
                            try {
                                println("${Thread.currentThread().name} - $id - get ${balancer.get()}")
                            } catch (e: IllegalStateException) {
                                val pause = Random.nextLong(100L, 5000L)
                                println("${Thread.currentThread().name} - $id - ${e.message} - paused $pause")
                                delay(pause)
                            }
                        }
                    }
                }
            }
        }
    }
}

internal class PrintableProvider (
    override val id: String = UUID.randomUUID().toString()
) : Provider {
    override fun get() = id

    override fun check(): Boolean {
        println("provider $id - health check")
        return true
    }
}
