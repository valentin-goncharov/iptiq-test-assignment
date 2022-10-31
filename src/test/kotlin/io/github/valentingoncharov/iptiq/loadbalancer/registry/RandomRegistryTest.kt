package io.github.valentingoncharov.iptiq.loadbalancer.registry

import io.mockk.every
import io.mockk.mockkObject
import io.mockk.unmockkObject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import kotlin.random.Random

@OptIn(ExperimentalCoroutinesApi::class)
internal class RandomRegistryTest {

    @Test
    fun `should add a new node if not in registry`() {
        val registry = RandomRegistry<String>()

        runTest {
            assertThat(registry.size).isEqualTo(0)
            assertThat(registry.add("Test1")).isTrue
            assertThat(registry.size).isEqualTo(1)
            assertThat(registry.add("Test2")).isTrue
            assertThat(registry.size).isEqualTo(2)
        }
    }

    @Test
    fun `should not add a new node if it's already in registry`() {
        val registry = RandomRegistry<String>()

        runTest {
            assertThat(registry.size).isEqualTo(0)
            assertThat(registry.add("Test1")).isTrue
            assertThat(registry.size).isEqualTo(1)
            assertThat(registry.add("Test1")).isFalse
            assertThat(registry.size).isEqualTo(1)
        }
    }

    @Test
    fun `should raise exception when no nodes in registry`() {
        val registry = RandomRegistry<String>()

        assertThatThrownBy { runTest { registry.next() } }
            .isInstanceOf(IllegalStateException::class.java)
            .hasMessage("Load balancer registry is empty")

    }

    @Test
    fun `should return only node`() {
        val registry = RandomRegistry<String>()

        runTest {
            assertThat(registry.add("Test1")).isTrue
            repeat(10) {
                assertThat(registry.next()).isEqualTo("Test1")
            }
        }
    }

    @Test
    fun `should return node by random generator`() {
        val registry = RandomRegistry<Int>()

        val indexes = IntArray(100) { Random.nextInt(6) }

        mockkObject(Random.Default)

        runTest {
            assertThat(registry.add(0)).isTrue
            assertThat(registry.add(1)).isTrue
            assertThat(registry.add(2)).isTrue
            assertThat(registry.add(3)).isTrue
            assertThat(registry.add(4)).isTrue
            assertThat(registry.add(5)).isTrue

            repeat(100) {
                val index = indexes[it]
                every { Random.nextInt(any()) } returns index
                assertThat(registry.next()).isEqualTo(index)
            }
        }

        unmockkObject(Random.Default)
    }


    @Test
    fun `should remove the existing node from the registry`() {
        val registry = RandomRegistry<String>()

        runTest {
            assertThat(registry.add("Test1")).isTrue
            assertThat(registry.add("Test2")).isTrue
            assertThat(registry.add("Test3")).isTrue
            assertThat(registry.size).isEqualTo(3)

            assertThat(registry.remove("Test2")).isTrue
            assertThat(registry.size).isEqualTo(2)
            assertThat(registry.remove("Test1")).isTrue
            assertThat(registry.size).isEqualTo(1)
            assertThat(registry.remove("Test3")).isTrue
            assertThat(registry.size).isEqualTo(0)
        }
    }

    @Test
    fun `should not remove non-existent node from the registry`() {
        val registry = RandomRegistry<String>()

        runTest {
            assertThat(registry.add("Test1")).isTrue
            assertThat(registry.add("Test2")).isTrue
            assertThat(registry.add("Test3")).isTrue
            assertThat(registry.size).isEqualTo(3)

            assertThat(registry.remove("Test4")).isFalse
            assertThat(registry.size).isEqualTo(3)
        }
    }
}
