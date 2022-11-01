package io.github.valentingoncharov.iptiq.provider

import java.util.UUID

data class Provider(override val id: String = UUID.randomUUID().toString()): IdProvider {
    override fun check() = true
}
