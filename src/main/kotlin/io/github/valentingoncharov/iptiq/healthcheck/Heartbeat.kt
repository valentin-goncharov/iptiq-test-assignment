package io.github.valentingoncharov.iptiq.healthcheck

interface Heartbeat {
    suspend fun check(): Boolean
}
