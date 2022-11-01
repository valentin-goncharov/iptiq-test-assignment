package io.github.valentingoncharov.iptiq.provider

import io.github.valentingoncharov.iptiq.healthcheck.Heartbeat

interface IdProvider: Heartbeat {
    val id: String

    fun get(): String = id
}
