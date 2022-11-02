package io.github.valentingoncharov.iptiq.helpers

import io.github.valentingoncharov.iptiq.provider.Provider
import java.util.UUID

internal class CountingHeartbeatProvider (
    override val id: String = UUID.randomUUID().toString()
) : Provider, CountingHeartbeat() {
    override fun get() = id
}
