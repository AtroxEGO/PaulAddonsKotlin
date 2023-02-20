package me.atroxego.pauladdons.features.dungeons

import PaulAddons.Companion.prefix
import me.atroxego.pauladdons.config.Config
import me.atroxego.pauladdons.events.impl.PacketEvent
import me.atroxego.pauladdons.features.Feature
import me.atroxego.pauladdons.utils.Utils.addMessage
import me.atroxego.pauladdons.utils.Utils.stripColor
import net.minecraft.network.play.server.S12PacketEntityVelocity
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

object Jerry : Feature() {

    @SubscribeEvent
    fun onPacket(event: PacketEvent.ReceiveEvent){
        if (!Config.jerryKB) return
        val heldItem = mc.thePlayer.heldItem
        if (heldItem != null && heldItem.displayName.stripColor().contains("Bonzo's Staff")) return
        try {
            if (event.packet.toString().contains("S12PacketEntityVelocity")){
                if ((event.packet as S12PacketEntityVelocity).entityID == mc.thePlayer.entityId){
                    mc.thePlayer.motionY = event.packet.motionY/8000.0
                    event.isCanceled = true
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            printdev("${prefix} Failed to apply Jerry Knockback")
        }
        }

    fun toggleJerry(){
        Config.jerryKB = !Config.jerryKB
        addMessage(if (Config.jerryKB) "$prefix Jerry: §aOn" else "$prefix Jerry: §cOff")
        mc.thePlayer.playSound("random.orb",1.0f, 0.7f)
    }
}