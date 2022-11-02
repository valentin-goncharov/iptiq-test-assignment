package io.github.valentingoncharov.iptiq.provider

import io.github.valentingoncharov.iptiq.healthcheck.Heartbeat

interface Provider: Heartbeat {
    val id: String

    fun get(): String
}
