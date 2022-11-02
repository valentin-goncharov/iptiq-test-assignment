package io.github.valentingoncharov.iptiq.provider

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class ProviderTest {

    @Test
    fun `should return provider id as string`() = runTest {
        val id = "Test 1"
        val provider = IdProvider(id)

        assertThat(provider.get()).isEqualTo(id)
    }

    @Test
    fun `should return true on check`() = runTest {
        val provider = IdProvider()

        assertThat(provider.check()).isTrue
    }
}
