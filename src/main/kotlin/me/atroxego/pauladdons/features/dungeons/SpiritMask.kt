package me.atroxego.pauladdons.features.dungeons

import PaulAddons
import PaulAddons.Companion.mc
import me.atroxego.pauladdons.config.Config
import me.atroxego.pauladdons.gui.GuiElement
import me.atroxego.pauladdons.render.font.FontUtils
import me.atroxego.pauladdons.utils.Utils.stripColor
import me.atroxego.pauladdons.utils.core.FloatPair
import net.minecraft.item.ItemStack
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent


object SpiritMask {

    init {
        SpiritMaskTimerGuiElement()
    }

    var hasSpiritInInv = false
    var nextSpiritUse = 0.0
    var spiritMaskItem : ItemStack? = null

    class SpiritMaskTimerGuiElement : GuiElement("Spirit Mask Timer", FloatPair(10, 10)) {
        override fun render() {
            if (!Config.spiritMaskTimer) return
            for (i in 0 until mc.thePlayer.inventory.sizeInventory){
                val itemStack = mc.thePlayer.inventory.getStackInSlot(i) ?: continue
                if (itemStack.displayName.stripColor().contains("Spirit Mask")){
                    hasSpiritInInv = true
                    spiritMaskItem = itemStack
                    break
                }
                hasSpiritInInv = false
                spiritMaskItem = null
            }
            if (!hasSpiritInInv) return
            if (spiritMaskItem == null) return
            val timeNow = (System.currentTimeMillis()/1000).toDouble()
            val text = if (nextSpiritUse - timeNow < 0) "§aReady" else FontUtils.getTimeBetween(timeNow, nextSpiritUse)
            val textX = FontUtils.smartFontPlacement(16f, text.stripColor(), this)
            FontUtils.smartTexturePlacement(0f, this, "pauladdons/SpiritMaskBasic.png", "pauladdons/SpiritMaskMirror.png")
            fr.drawString(text, textX, 5f, 0xFFFFFF, true)

        }

        override fun demoRender() {
            val textX = FontUtils.smartFontPlacement(16f, "READY", this)
            FontUtils.smartTexturePlacement(0f, this, "pauladdons/SpiritMaskBasic.png", "pauladdons/SpiritMaskMirror.png")
            fr.drawString("§aREADY", textX, 5f, 0xFFFFFF, true)
        }

        override val toggled: Boolean
            get() = Config.spiritMaskTimer
        override val height: Int
            get() = fr.FONT_HEIGHT
        override val width: Int
            get() = 20 + fr.getStringWidth("READY")

        init {
            PaulAddons.guiManager.registerElement(this)
        }
    }

    @SubscribeEvent
    fun onWorldChange(event: WorldEvent.Load?) {
        nextSpiritUse = 0.0
    }
    @SubscribeEvent
    fun onChat(event: ClientChatReceivedEvent){
        val message = event.message.unformattedText
        if (spiritMaskItem == null) return
        if (message != "Second Wind Activated! Your Spirit Mask saved your life!") return
        val usedTime = (System.currentTimeMillis() / 1000).toDouble()
        nextSpiritUse = usedTime + 30
    }
}