package io.github.valentingoncharov.iptiq.provider

import java.util.UUID

data class Provider(val id: String = UUID.randomUUID().toString()): IdProvider {
    override suspend fun get(): String  = id
}
