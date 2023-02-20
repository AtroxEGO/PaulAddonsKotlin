package me.atroxego.pauladdons.features.other

import me.atroxego.pauladdons.events.impl.PacketEvent
import me.atroxego.pauladdons.features.Feature
import net.minecraft.network.play.client.C16PacketClientStatus
import net.minecraft.network.play.server.S01PacketJoinGame
import net.minecraft.network.play.server.S37PacketStatistics
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import kotlin.math.absoluteValue

object Ping : Feature(){
    var lastPingTime = -1L
    var ping = -1.0

    @SubscribeEvent
    fun onPacket(event: PacketEvent.ReceiveEvent){
        if (lastPingTime > 0){
            if (event.packet is S01PacketJoinGame) lastPingTime = -1L
            if (event.packet is S37PacketStatistics) {
                val diff = (kotlin.math.abs(System.nanoTime() - lastPingTime) / 1_000_000.0)
                lastPingTime *= -1
                ping = diff
            }
        }
        if (lastPingTime < 0 && (mc.currentScreen != null || mc.thePlayer != null) && System.nanoTime()
            - lastPingTime.absoluteValue > 1_000_000L * 5_000
        ) {
            sendPing()
        }
    }

    fun sendPing() {
        mc.thePlayer.sendQueue.networkManager.sendPacket(C16PacketClientStatus(C16PacketClientStatus.EnumState.REQUEST_STATS), { lastPingTime = System.nanoTime()}
        )
    }

}
