package io.github.valentingoncharov.iptiq.healthcheck

import io.github.valentingoncharov.iptiq.loadbalancer.registry.Registry
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

const val DEFAULT_HEALTH_CHECK_DELAY = 1000L

class HealthCheck<T: Heartbeat>(
    private val registry: Registry<T>,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) {

    private val healthChecks = mutableMapOf<T, Job>()

    fun start(provider: T) {
        healthChecks[provider] ?: let {
            healthChecks[provider] = launchHealthCheck(provider)
        }
    }

    fun stop(provider: T) = healthChecks.remove(provider)?.cancel()

    private fun launchHealthCheck(provider: T): Job = CoroutineScope(dispatcher).launch {
        while (true) {
            delay(DEFAULT_HEALTH_CHECK_DELAY)
            if (!provider.check()) {
                registry.remove(provider)
            }
        }
    }
}
