package io.github.valentingoncharov.iptiq.healthcheck

import io.github.valentingoncharov.iptiq.loadbalancer.registry.Registry
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

const val DEFAULT_HEALTH_CHECK_DELAY = 1000L

class HealthCheck<T: Heartbeat>(
    private val registry: Registry<T>,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) {

    private val healthChecks = mutableMapOf<T, Job>()

    suspend fun start(provider: T) = withContext(dispatcher) {
        healthChecks[provider] ?: let {
            healthChecks[provider] = launchHealthCheck(provider)
        }
    }

    suspend fun stop(provider: T) = withContext(dispatcher){
        healthChecks.remove(provider)?.cancel()
    }

    private fun launchHealthCheck(provider: T): Job = CoroutineScope(dispatcher).launch {
        while (true) {
            delay(DEFAULT_HEALTH_CHECK_DELAY)
            if (!provider.check()) {
                registry.remove(provider)
            }
        }
    }
}
