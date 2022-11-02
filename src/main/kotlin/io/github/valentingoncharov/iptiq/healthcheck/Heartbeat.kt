package io.github.valentingoncharov.iptiq.healthcheck

interface Heartbeat {
    fun check(): Boolean
}
