package io.github.valentingoncharov.iptiq.loadbalancer

import io.github.valentingoncharov.iptiq.provider.Provider
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test


internal class LoadBalancerTest {

    @Test
    fun `should register new provider`() {
        val loadBalancer = LoadBalancer()
        assertThat(loadBalancer.register(Provider())).isTrue
    }

    @Test
    fun `should register defined number of providers`() {
        val loadBalancer = LoadBalancer(2)
        assertThat(loadBalancer.register(Provider())).isTrue
        assertThat(loadBalancer.register(Provider())).isTrue
        assertThat(loadBalancer.register(Provider())).isFalse
    }

    @Test
    fun `should register default number of providers`() {
        val loadBalancer = LoadBalancer()
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
