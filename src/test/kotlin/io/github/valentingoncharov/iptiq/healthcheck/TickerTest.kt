package io.github.valentingoncharov.iptiq.healthcheck

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class TickerTest {

    @Test
    fun `should reset ticker into initial state and call provided lambda`() {
        val ticksNumber = 3
        val ticker = Ticker(ticksNumber)
        runTest {
            var isResetFnCalled = false
            var isTickFnCalled = false

            ticker.tick { isTickFnCalled = true }
            assertThat(isTickFnCalled).isFalse

            ticker.reset { isResetFnCalled = true }
            assertThat(isResetFnCalled).isTrue

            repeat(ticksNumber){
                ticker.tick{ isTickFnCalled = true }
            }
            assertThat(isTickFnCalled).isTrue
        }
    }

    @Test
    fun `should call provided lambda only after defined number of ticks`() {
        val ticksNumber = 5
        val ticker = Ticker(ticksNumber)
        runTest {
            var isTickFnCalled = false

            repeat(ticksNumber - 1) {
                ticker.tick{ isTickFnCalled = true }
                assertThat(isTickFnCalled).isFalse
            }
            ticker.tick{ isTickFnCalled = true }
            assertThat(isTickFnCalled).isTrue
        }
    }

    @Test
    fun `should call provided lambda only once`() {
        val ticksNumber = 3
        val ticker = Ticker(ticksNumber)
        runTest {
            var numberTickFnCalled = 0

            repeat(ticksNumber * 3) {
                ticker.tick{ numberTickFnCalled++ }
            }
            assertThat(numberTickFnCalled).isEqualTo(1)
        }
    }
}
