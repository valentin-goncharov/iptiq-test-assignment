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
const val DEFAULT_SUCCESS_CHECK_NUMBER = 2

class HealthCheck<T: Heartbeat>(
    private val registry: Registry<T>,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) {

    private val healthChecks = mutableMapOf<T, Job>()

    suspend fun start(heartbeat: T) = withContext(dispatcher) {
        healthChecks[heartbeat] ?: let {
            healthChecks[heartbeat] = launchHealthCheck(heartbeat)
        }
    }

    suspend fun stop(heartbeat: T) = withContext(dispatcher){
        healthChecks.remove(heartbeat)?.cancel()
    }

    private fun launchHealthCheck(heartbeat: T): Job = CoroutineScope(dispatcher).launch {
        val ticker = Ticker(DEFAULT_SUCCESS_CHECK_NUMBER)
        while (true) {
            delay(DEFAULT_HEALTH_CHECK_DELAY)
            if (!heartbeat.check()) {
                ticker.reset { registry.remove(heartbeat) }
            } else {
                ticker.tick { registry.add(heartbeat) }
            }
        }
    }
}
