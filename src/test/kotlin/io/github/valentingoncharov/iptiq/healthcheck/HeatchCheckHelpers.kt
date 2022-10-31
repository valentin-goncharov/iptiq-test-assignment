package io.github.valentingoncharov.iptiq.healthcheck

internal class FailHeartbeat: Heartbeat{
    override fun check() = false
}
