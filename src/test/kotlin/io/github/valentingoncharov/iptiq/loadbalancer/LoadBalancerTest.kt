package io.github.valentingoncharov.iptiq.loadbalancer

import io.github.valentingoncharov.iptiq.loadbalancer.registry.RandomRegistry
import io.github.valentingoncharov.iptiq.provider.Provider
import io.mockk.coVerify
import io.mockk.spyk
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class LoadBalancerTest {

    @Test
    fun `should register new provider`() {
        val registry = spyk(RandomRegistry<Provider>())
        val loadBalancer = LoadBalancer(registry)
        runTest {
            val provider = Provider("Test 1")
            assertThat(loadBalancer.register(provider)).isTrue
            coVerify { registry.add(provider) }
        }
    }

    @Test
    fun `should register defined number of providers`() {
        val loadBalancer = LoadBalancer(registry = RandomRegistry(), capacity = 2)
        runTest {
            assertThat(loadBalancer.register(Provider())).isTrue
            assertThat(loadBalancer.register(Provider())).isTrue
            assertThat(loadBalancer.register(Provider())).isFalse
        }
    }

    @Test
    fun `should register default number of providers`() {
        val loadBalancer = LoadBalancer(RandomRegistry())
        runTest {
            assertThat(loadBalancer.register(Provider())).isTrue
            assertThat(loadBalancer.register(Provider())).isTrue
            assertThat(loadBalancer.register(Provider())).isTrue
            assertThat(loadBalancer.register(Provider())).isTrue
            assertThat(loadBalancer.register(Provider())).isTrue
            assertThat(loadBalancer.register(Provider())).isTrue
            assertThat(loadBalancer.register(Provider())).isTrue
            assertThat(loadBalancer.register(Provider())).isTrue
            assertThat(loadBalancer.register(Provider())).isTrue
            assertThat(loadBalancer.register(Provider())).isTrue
            assertThat(loadBalancer.register(Provider())).isFalse
        }
    }

    @Test
    fun `should get next provider from registry and call get method`() {
        val providerId1 = "Test 1"
        val providerId2 = "Test 2"
        val providerId3 = "Test 3"
        val registry = spyk(RandomRegistry<Provider>())
        val loadBalancer = LoadBalancer(registry)
        runTest {
            assertThat(loadBalancer.register(Provider(providerId1))).isTrue
            assertThat(loadBalancer.register(Provider(providerId2))).isTrue
            assertThat(loadBalancer.register(Provider(providerId3))).isTrue
            assertThat(registry.size).isEqualTo(3)

            repeat(10) {
                assertThat(loadBalancer.get()).isIn(providerId1, providerId2, providerId3)
            }

            coVerify(exactly = 10) { registry.next() }
        }
    }
}
