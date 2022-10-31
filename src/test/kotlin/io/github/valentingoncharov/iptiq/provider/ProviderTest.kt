package io.github.valentingoncharov.iptiq.provider

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.UUID

@OptIn(ExperimentalCoroutinesApi::class)
internal class ProviderTest {

    @Test
    fun `should return provider id as string`() = runTest {
        val id = "Test 1"
        val provider = Provider(id)

        assertThat(provider.get()).isEqualTo(id)
    }

    @Test
    fun `should return true on check`() = runTest {
        val provider = Provider()

        assertThat(provider.check()).isTrue
    }
}
