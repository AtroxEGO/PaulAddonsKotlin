package me.atroxego.pauladdons.features.slayers


import me.atroxego.pauladdons.config.Config
import me.atroxego.pauladdons.events.impl.PacketEvent
import me.atroxego.pauladdons.features.Feature
import me.atroxego.pauladdons.gui.GuiElement
import me.atroxego.pauladdons.gui.GuiManager
import me.atroxego.pauladdons.utils.SBInfo
import me.atroxego.pauladdons.utils.Utils
import me.atroxego.pauladdons.utils.Utils.stripColor
import net.minecraft.block.Block
import net.minecraft.network.play.server.S22PacketMultiBlockChange
import net.minecraft.util.BlockPos
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import java.awt.Color

object TotemTimer : Feature(){

    var delay = 7200

    init {
        TotemTimerGUI()
    }

    var totemBoomTimestamp = -1L

    class TotemTimerGUI : GuiElement("Totem Timer") {
        override fun render() {
            if (totemBoomTimestamp != -1L){
                val timeLeft = totemBoomTimestamp - System.currentTimeMillis()
                val text = getTimeText(timeLeft)
                if (timeLeft > 0){

                    fr.drawString(text,2,5, Color.WHITE.rgb)
                } else totemBoomTimestamp = -1L
            }
        }

        override fun demoRender() {
            fr.drawString("§e4712ms",2,5, Color.WHITE.rgb)
        }

        override val toggled: Boolean
            get() = Config.totemTimer
        override val height: Int
            get() = 9
        override val width: Int
            get() = 40


        init { GuiManager.registerElement(this) }
    }

    fun getTimeText(timeLeft: Long): String{
        val color: String = if (timeLeft > 5000) "§a"
        else if (timeLeft > 2000) "§e"
        else "§c"

        return color + timeLeft + "ms"
    }

    private var totemPos : BlockPos? = null

    @SubscribeEvent
    fun onPacket(event: PacketEvent.ReceiveEvent){
        if (!Config.totemTimer || totemBoomTimestamp != -1L) return
        if (mc.thePlayer == null) return
        if (mc.currentScreen != null) return
        if (SBInfo.mode != "crimson_isle") return
        if (Utils.getScoreboardLines().size < 5) return
        if (!Utils.getScoreboardLines()[Utils.getScoreboardLines().size - 3].stripColor().contains("Slay the boss!")) return
        if (event.packet !is S22PacketMultiBlockChange) return
        for (block in event.packet.changedBlocks){
            if (mc.thePlayer.getDistance(block.pos.x.toDouble(), block.pos.y.toDouble(), block.pos.z.toDouble()) > 25) return
            if (totemPos != null) {
                if (block.pos == totemPos && Block.getStateId(block.blockState) == 0){
                    totemPos = null
                    totemBoomTimestamp = -1L
                }
            }
            if (Block.getStateId(block.blockState) == 16543){
                totemBoomTimestamp = System.currentTimeMillis() + delay
                totemPos = block.pos
                printdev("Detected Totem At: ${block.pos}")
            }
        }
    }
}