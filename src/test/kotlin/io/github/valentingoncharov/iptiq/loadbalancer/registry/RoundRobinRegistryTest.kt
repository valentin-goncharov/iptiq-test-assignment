package io.github.valentingoncharov.iptiq.loadbalancer.registry

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class RoundRobinRegistryTest {

    @Test
    fun `should add a new node if not in registry`() {
        val registry = RoundRobinRegistry<String>()

        runTest {
            Assertions.assertThat(registry.size).isEqualTo(0)
            Assertions.assertThat(registry.add("Test1")).isTrue
            Assertions.assertThat(registry.size).isEqualTo(1)
            Assertions.assertThat(registry.add("Test2")).isTrue
            Assertions.assertThat(registry.size).isEqualTo(2)
        }
    }

    @Test
    fun `should not add a new node if it's already in registry`() {
        val registry = RoundRobinRegistry<String>()

        runTest {
            Assertions.assertThat(registry.size).isEqualTo(0)
            Assertions.assertThat(registry.add("Test1")).isTrue
            Assertions.assertThat(registry.size).isEqualTo(1)
            Assertions.assertThat(registry.add("Test1")).isFalse
            Assertions.assertThat(registry.size).isEqualTo(1)
        }
    }

    @Test
    fun `should raise exception when no nodes in registry`() {
        val registry = RoundRobinRegistry<String>()


        Assertions.assertThatThrownBy { runTest { registry.next() } }
            .isInstanceOf(IllegalStateException::class.java)
            .hasMessage("Load balancer registry is empty")

    }

    @Test
    fun `should return only node`() {
        val registry = RoundRobinRegistry<String>()

        runTest {
            Assertions.assertThat(registry.add("Test1")).isTrue
            repeat(100) {
                Assertions.assertThat(registry.next()).isEqualTo("Test1")
            }
        }
    }

    @Test
    fun `should return next node`() {
        val registry = RoundRobinRegistry<Int>()

        runTest {
            Assertions.assertThat(registry.add(0)).isTrue
            Assertions.assertThat(registry.add(1)).isTrue
            Assertions.assertThat(registry.add(2)).isTrue
            Assertions.assertThat(registry.add(3)).isTrue
            Assertions.assertThat(registry.add(4)).isTrue
            Assertions.assertThat(registry.add(5)).isTrue

            repeat(100) {
                Assertions.assertThat(registry.next()).isEqualTo(it % 6)
            }
        }
    }

    @Test
    fun `should return next node if removed node index greater than last one`() {
        val registry = RoundRobinRegistry<Int>()

        runTest {
            Assertions.assertThat(registry.add(0)).isTrue
            Assertions.assertThat(registry.add(1)).isTrue
            Assertions.assertThat(registry.add(2)).isTrue
            Assertions.assertThat(registry.add(3)).isTrue
            Assertions.assertThat(registry.add(4)).isTrue
            Assertions.assertThat(registry.add(5)).isTrue

            Assertions.assertThat(registry.next()).isEqualTo(0)
            Assertions.assertThat(registry.next()).isEqualTo(1)

            Assertions.assertThat(registry.remove(4)).isTrue

            Assertions.assertThat(registry.next()).isEqualTo(2)
        }
    }

    @Test
    fun `should return next node if removed node index equals last one`() {
        val registry = RoundRobinRegistry<Int>()

        runTest {
            Assertions.assertThat(registry.add(0)).isTrue
            Assertions.assertThat(registry.add(1)).isTrue
            Assertions.assertThat(registry.add(2)).isTrue
            Assertions.assertThat(registry.add(3)).isTrue
            Assertions.assertThat(registry.add(4)).isTrue
            Assertions.assertThat(registry.add(5)).isTrue

            Assertions.assertThat(registry.next()).isEqualTo(0)
            Assertions.assertThat(registry.next()).isEqualTo(1)
            Assertions.assertThat(registry.next()).isEqualTo(2)
            Assertions.assertThat(registry.next()).isEqualTo(3)
            Assertions.assertThat(registry.next()).isEqualTo(4)
            Assertions.assertThat(registry.next()).isEqualTo(5)

            Assertions.assertThat(registry.remove(5)).isTrue

            Assertions.assertThat(registry.next()).isEqualTo(0)
        }
    }

    @Test
    fun `should return next node if removed node index less than last one`() {
        val registry = RoundRobinRegistry<Int>()

        runTest {
            Assertions.assertThat(registry.add(0)).isTrue
            Assertions.assertThat(registry.add(1)).isTrue
            Assertions.assertThat(registry.add(2)).isTrue
            Assertions.assertThat(registry.add(3)).isTrue
            Assertions.assertThat(registry.add(4)).isTrue
            Assertions.assertThat(registry.add(5)).isTrue

            Assertions.assertThat(registry.next()).isEqualTo(0)
            Assertions.assertThat(registry.next()).isEqualTo(1)
            Assertions.assertThat(registry.next()).isEqualTo(2)
            Assertions.assertThat(registry.next()).isEqualTo(3)
            Assertions.assertThat(registry.next()).isEqualTo(4)
            Assertions.assertThat(registry.next()).isEqualTo(5)

            Assertions.assertThat(registry.remove(0)).isTrue

            Assertions.assertThat(registry.next()).isEqualTo(1)
        }
    }


    @Test
    fun `should remove the existing node from the registry`() {
        val registry = RoundRobinRegistry<String>()

        runTest {
            Assertions.assertThat(registry.add("Test1")).isTrue
            Assertions.assertThat(registry.add("Test2")).isTrue
            Assertions.assertThat(registry.add("Test3")).isTrue
            Assertions.assertThat(registry.size).isEqualTo(3)

            Assertions.assertThat(registry.remove("Test2")).isTrue
            Assertions.assertThat(registry.size).isEqualTo(2)
            Assertions.assertThat(registry.remove("Test1")).isTrue
            Assertions.assertThat(registry.size).isEqualTo(1)
            Assertions.assertThat(registry.remove("Test3")).isTrue
            Assertions.assertThat(registry.size).isEqualTo(0)
        }
    }

    @Test
    fun `should not remove non-existent node from the registry`() {
        val registry = RoundRobinRegistry<String>()

        runTest {
            Assertions.assertThat(registry.add("Test1")).isTrue
            Assertions.assertThat(registry.add("Test2")).isTrue
            Assertions.assertThat(registry.add("Test3")).isTrue
            Assertions.assertThat(registry.size).isEqualTo(3)

            Assertions.assertThat(registry.remove("Test4")).isFalse
            Assertions.assertThat(registry.size).isEqualTo(3)
        }
    }
}
