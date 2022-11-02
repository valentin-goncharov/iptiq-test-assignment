package io.github.valentingoncharov.iptiq.provider

import io.github.valentingoncharov.iptiq.healthcheck.Heartbeat
import java.util.UUID

data class IdProvider(override val id: String = UUID.randomUUID().toString()): Provider, Heartbeat {

    override fun get(): String = id

    override fun check() = true
}
