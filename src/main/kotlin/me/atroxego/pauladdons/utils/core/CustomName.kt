package me.atroxego.pauladdons.utils.core

import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

class CustomName(val name: String, private val animated: Boolean, private val nicks: List<Nick>) {
    private var time = -1L
    private var currentIndex = 0

    init {
        MinecraftForge.EVENT_BUS.register(this)
    }

    @SubscribeEvent
    fun onWorldRender(event: RenderWorldLastEvent) {
        if (!animated) return
        val currentTime = System.currentTimeMillis()
        if (time == -1L) time = currentTime
        else if (currentTime - time > nicks[currentIndex].delay) {
            if (++currentIndex >= nicks.size) currentIndex = 0
            time = currentTime
            Cosmetics.namesCache.entries.removeIf { it.key.contains(name) }
        }
    }

    fun getNick(): Nick = nicks[currentIndex]

    data class Nick(val nick: String, val prefix: String, val delay: Int)
}