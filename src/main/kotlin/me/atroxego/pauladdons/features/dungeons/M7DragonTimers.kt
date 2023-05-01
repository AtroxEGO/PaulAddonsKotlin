package me.atroxego.pauladdons.features.dungeons

import me.atroxego.pauladdons.PaulAddons.Companion.prefix
import gg.essential.universal.UChat
import gg.essential.universal.UMatrixStack
import me.atroxego.pauladdons.config.Config
import me.atroxego.pauladdons.events.impl.PacketEvent
import me.atroxego.pauladdons.features.Feature
import me.atroxego.pauladdons.render.RenderUtils.renderWaypointText
import me.atroxego.pauladdons.utils.Utils
import me.atroxego.pauladdons.utils.Utils.inDungeon
import net.minecraft.network.play.server.S2APacketParticles
import net.minecraft.util.Vec3
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

object M7DragonTimers : Feature(){

    data class Dragon(val type: DragonType, var timestamp: Long, var display: Boolean, var name: String)

    private val dragons = listOf(
        Dragon(DragonType.SOUL,0L, false, "§5Soul"),
        Dragon(DragonType.FLAME,0L, false, "§6Flame"),
        Dragon(DragonType.APEX,0L, false, "§aApex"),
        Dragon(DragonType.POWER,0L, false, "§4Power"),
        Dragon(DragonType.ICE,0L, false, "§bIce"),
    )

    enum class DragonType {
        SOUL, FLAME, APEX, POWER, ICE
    }

    val dragonPositions = mapOf(
        "SOUL" to Vec3(56.5,8.0,125.5),
        "FLAME" to Vec3(85.5,8.0,56.5),
        "APEX" to Vec3(27.5,8.0,96.5),
        "POWER" to Vec3(27.5,8.0,59.5),
        "ICE" to Vec3(84.5,8.0,94.5),
    )

    @SubscribeEvent
    fun onPacket(event: PacketEvent.ReceiveEvent){
        if (!inDungeon) return
        if (!Config.dragonTimers) return
        if (event.packet !is S2APacketParticles) return
        val packet = event.packet
        if (Utils.getScoreboardLines().size < 3) return
        if(!Utils.getScoreboardLines()[3].contains("M7")) return
        if (mc.thePlayer.posY > 20) return

        for (dragon in dragons){
            if (dragon.display) continue
            val pos = dragonPositions[dragon.type.name]!!
            if (packet.xCoordinate in pos.xCoord - 5 .. pos.xCoord + 5 && packet.yCoordinate in pos.yCoord - 3 .. pos.yCoord + 3 && packet.zCoordinate in pos.zCoord - 5..pos.zCoord + 5 ){
                dragon.timestamp = System.currentTimeMillis() + 5000
                dragon.display = true
                UChat.chat("$prefix ${dragon.name} is Spawning!")
            }
        }
    }

    @SubscribeEvent
    fun worldRender(event: RenderWorldLastEvent){
        for (dragon in dragons) {
            if (!dragon.display) continue
            if (dragon.timestamp - System.currentTimeMillis() < 0) {
                dragon.display = false
                continue
            }
            val pos = dragonPositions[dragon.type.name]!!
            renderWaypointText("${dragon.name} Dragon in: ${dragon.timestamp - System.currentTimeMillis()}", pos.xCoord,pos.yCoord,pos.zCoord, event.partialTicks, UMatrixStack(), distance = false, scale = 2f)
        }
    }

}