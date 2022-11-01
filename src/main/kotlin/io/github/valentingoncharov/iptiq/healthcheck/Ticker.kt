package io.github.valentingoncharov.iptiq.healthcheck

internal class Ticker(private val original: Int) {

    private var internalCounter = original

    suspend fun reset(fn: suspend () -> Unit) {
        internalCounter = original
        fn()
    }

    suspend fun tick(fn: suspend () -> Unit) {
        if(internalCounter > 0 ){
            internalCounter--
            if (internalCounter == 0) {
                fn()
            }
        }
    }
}
