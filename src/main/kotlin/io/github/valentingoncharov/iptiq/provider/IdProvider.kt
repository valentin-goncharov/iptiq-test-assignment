package io.github.valentingoncharov.iptiq.provider

interface IdProvider {
    suspend fun get(): String
}
