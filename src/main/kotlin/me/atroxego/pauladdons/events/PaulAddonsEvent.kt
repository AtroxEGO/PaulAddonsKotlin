package me.atroxego.pauladdons.events

import PaulAddons
import gg.essential.universal.UChat
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.Event

abstract class PaulAddonsEvent : Event() {
    val eventName by lazy {
        this::class.simpleName
    }

    fun postAndCatch(): Boolean {
        return runCatching {
            MinecraftForge.EVENT_BUS.post(this)
        }.onFailure {
            it.printStackTrace()
            UChat.chat("${PaulAddons.prefix} Error :o")
        }.getOrDefault(isCanceled)
    }
}