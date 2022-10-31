package io.github.valentingoncharov.iptiq.provider

import io.github.valentingoncharov.iptiq.healthcheck.Heartbeat
import java.util.UUID

data class Provider(val id: String = UUID.randomUUID().toString()): Heartbeat {
    fun get(): String  = id

    override fun check() = true
}
