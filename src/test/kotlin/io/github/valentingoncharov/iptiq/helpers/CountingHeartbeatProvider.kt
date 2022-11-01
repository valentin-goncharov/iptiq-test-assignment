package io.github.valentingoncharov.iptiq.helpers

import io.github.valentingoncharov.iptiq.provider.IdProvider
import java.util.UUID

internal class CountingHeartbeatProvider (
    override val id: String = UUID.randomUUID().toString()
) : IdProvider, CountingHeartbeat() {
    override fun get() = id
}

