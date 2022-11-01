package io.github.valentingoncharov.iptiq.helpers

import io.github.valentingoncharov.iptiq.healthcheck.Heartbeat
import io.github.valentingoncharov.iptiq.provider.IdProvider
import java.util.UUID

internal class FailHeartbeat: Heartbeat {
    override fun check() = false
}

internal open class CountingHeartbeat(var counter: Int = 0): Heartbeat {
    override fun check(): Boolean {
        counter += 1
        return true
    }
}
