package io.github.valentingoncharov.iptiq.loadbalancer

import io.github.valentingoncharov.iptiq.loadbalancer.registry.TestGenericRegistry
import io.github.valentingoncharov.iptiq.provider.Provider
import io.mockk.coVerify
import io.mockk.spyk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.UUID

@OptIn(ExperimentalCoroutinesApi::class)
internal class LoadBalancerTest {

    @Test
    fun `should register new provider`() {
        val registry = TestGenericRegistry<Provider>()
        val loadBalancer = LoadBalancer(registry)
        runTest {
            val provider = Provider("Test 1")
            assertThat(registry.size).isEqualTo(0)
            assertThat(loadBalancer.register(provider)).isTrue
            assertThat(registry.size).isEqualTo(1)
        }
    }

    @Test
    fun `should register defined number of providers`() {
        val registry = TestGenericRegistry<Provider>()
        val loadBalancer = LoadBalancer(registry = registry, capacity = 2)
        runTest {
            assertThat(loadBalancer.register(Provider())).isTrue
            assertThat(loadBalancer.register(Provider())).isTrue
            assertThat(loadBalancer.register(Provider())).isFalse
            assertThat(registry.size).isEqualTo(2)
        }
    }

    @Test
    fun `should register default number of providers`() {
        val registry = TestGenericRegistry<Provider>()
        val loadBalancer = LoadBalancer(registry)
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
            assertThat(registry.size).isEqualTo(10)
        }
    }

    @Test
    fun `should get next provider from registry and call get method`() {
        val providerId1 = "Test 1"
        val providerId2 = "Test 2"
        val providerId3 = "Test 3"
        val registry = spyk(TestGenericRegistry<Provider>())
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

    @Test
    fun `should return false when include not registered provider`() {
        val loadBalancer = LoadBalancer(TestGenericRegistry())

        runTest {
            assertThat(loadBalancer.register(Provider())).isTrue

            assertThat(loadBalancer.include(UUID.randomUUID().toString())).isFalse
        }
    }


    @Test
    fun `should exclude provider from load balancing`() {
        val registry = TestGenericRegistry<Provider>()
        val loadBalancer = LoadBalancer(registry)

        val providerId = UUID.randomUUID().toString()

        runTest {
            assertThat(loadBalancer.register(Provider(providerId))).isTrue
            assertThat(registry.size).isEqualTo(1)

            assertThat(loadBalancer.exclude(providerId)).isTrue
            assertThat(registry.size).isEqualTo(0)
        }
    }

    @Test
    fun `should return false when exclude not registered provider`() {
        val loadBalancer = LoadBalancer(TestGenericRegistry())
        runTest {
            assertThat(loadBalancer.register(Provider())).isTrue

            assertThat(loadBalancer.exclude(UUID.randomUUID().toString())).isFalse
        }
    }

    @Test
    fun `return the included provider to load balancing`() {
        val loadBalancer = LoadBalancer(TestGenericRegistry())

        val providerId = UUID.randomUUID().toString()
        runTest {
            assertThat(loadBalancer.register(Provider(providerId))).isTrue

            assertThat(loadBalancer.exclude(providerId)).isTrue

            assertThat(loadBalancer.include(providerId)).isTrue

            assertThat(loadBalancer.get()).isEqualTo(providerId)
        }
    }

    @Test
    fun `should return false when trying to include provider but load balancer capacity is exceeded`() {
        val loadBalancer = LoadBalancer(registry = TestGenericRegistry(), capacity = 1)

        val providerId = UUID.randomUUID().toString()

        runTest {
            assertThat(loadBalancer.register(Provider(providerId))).isTrue

            assertThat(loadBalancer.exclude(providerId)).isTrue

            assertThat(loadBalancer.register(Provider())).isTrue

            assertThat(loadBalancer.include(providerId)).isFalse
        }
    }
}
