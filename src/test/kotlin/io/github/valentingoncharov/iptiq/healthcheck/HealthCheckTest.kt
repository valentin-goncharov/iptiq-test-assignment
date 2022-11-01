package io.github.valentingoncharov.iptiq.healthcheck

import io.github.valentingoncharov.iptiq.helpers.CountingHeartbeat
import io.github.valentingoncharov.iptiq.helpers.FailHeartbeat
import io.github.valentingoncharov.iptiq.loadbalancer.registry.Registry
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.spyk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class HealthCheckTest {

    @Test
    fun `should remove provider from registry when check fails`() {
        val dispatcher = StandardTestDispatcher()
        runTest(dispatcher) {
            val registry = mockk<Registry<FailHeartbeat>>(relaxed = true)
            val healthCheck = HealthCheck(registry, dispatcher = dispatcher )
            val provider = FailHeartbeat()

            healthCheck.start(provider)
            advanceTimeBy(DEFAULT_HEALTH_CHECK_DELAY + 1L)
            coVerify(atLeast = 1) { registry.remove(provider) }
            healthCheck.stop(provider)
        }
    }

    @Test
    fun `should wait default timeout before call provider check`() {
        val dispatcher = StandardTestDispatcher()
        runTest(dispatcher) {
            val registry = mockk<Registry<FailHeartbeat>>(relaxed = true)
            val healthCheck = HealthCheck(registry, dispatcher = dispatcher)
            val provider = spyk(FailHeartbeat())

            healthCheck.start(provider)
            advanceTimeBy(DEFAULT_HEALTH_CHECK_DELAY)
            coVerify(exactly = 0) { provider.check() }
            advanceTimeBy(1L)
            coVerify(exactly = 1) { provider.check() }
            healthCheck.stop(provider)
        }
    }

    @Test
    fun `should cancel job on stop`() {
        val dispatcher = StandardTestDispatcher()
        runTest(dispatcher) {
            val registry = mockk<Registry<CountingHeartbeat>>(relaxed = true)
            val healthCheck = HealthCheck(registry, dispatcher = dispatcher )
            val provider = CountingHeartbeat()

            healthCheck.start(provider)
            advanceTimeBy(DEFAULT_HEALTH_CHECK_DELAY + 1L)
            Assertions.assertThat(provider.counter).isEqualTo(1)
            healthCheck.stop(provider)
            advanceTimeBy(10L * DEFAULT_HEALTH_CHECK_DELAY)
            Assertions.assertThat(provider.counter).isEqualTo(1)
        }
    }
}
