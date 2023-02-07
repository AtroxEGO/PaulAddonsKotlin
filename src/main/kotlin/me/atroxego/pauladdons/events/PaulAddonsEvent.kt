package me.atroxego.pauladdons.events

import PaulAddons
import gg.essential.universal.UChat
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.Event

abstract class PaulAddonsEvent : Event() {
    private val eventName by lazy {
        this::class.simpleName
    }

    fun postAndCatch(): Boolean {
        return runCatching {
            MinecraftForge.EVENT_BUS.post(this)
        }.onFailure {
            it.printStackTrace()
            UChat.chat("${PaulAddons.prefix} caught and logged an ${it::class.simpleName ?: "error"} at ${eventName}. Please report this on the Discord server at https://discord.gg/qVamggFna6.")
        }.getOrDefault(isCanceled)
    }
}