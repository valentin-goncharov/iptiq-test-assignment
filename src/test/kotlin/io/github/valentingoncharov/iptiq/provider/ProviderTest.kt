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
        val id = UUID.randomUUID().toString()
        val provider = Provider(id)

        assertThat(provider.get()).isEqualTo(id)
    }
}
