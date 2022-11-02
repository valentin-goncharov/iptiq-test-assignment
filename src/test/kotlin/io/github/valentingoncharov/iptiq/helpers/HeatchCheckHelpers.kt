package io.github.valentingoncharov.iptiq.helpers

import io.github.valentingoncharov.iptiq.healthcheck.Heartbeat

internal class FailHeartbeat: Heartbeat {
    override fun check() = false
}

internal class SuccessHeartbeat: Heartbeat {
    override fun check() = true
}


internal open class CountingHeartbeat(var counter: Int = 0): Heartbeat {
    override fun check(): Boolean {
        counter += 1
        return true
    }
}
