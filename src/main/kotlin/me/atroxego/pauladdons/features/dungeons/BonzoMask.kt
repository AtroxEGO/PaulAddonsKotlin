package me.atroxego.pauladdons.features.dungeons

import PaulAddons
import PaulAddons.Companion.mc
import me.atroxego.pauladdons.config.Config
import me.atroxego.pauladdons.features.betterlootshare.ESP.logger
import me.atroxego.pauladdons.gui.GuiElement
import me.atroxego.pauladdons.render.font.FontUtils
import me.atroxego.pauladdons.utils.Utils
import me.atroxego.pauladdons.utils.Utils.stripColor
import me.atroxego.pauladdons.utils.core.FloatPair
import net.minecraft.item.ItemStack
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent


object BonzoMask {

    init {
        BonzoMaskTimerGuiElement()
    }

    var hasBonzoInInv = false
    var nextBonzoUse = 0.0
    var bonzoMaskItem : ItemStack? = null

    class BonzoMaskTimerGuiElement : GuiElement("Bonzo Mask Timer", FloatPair(10, 10)) {
        override fun render() {
            if (!Config.bonzoMaskTimer || !Utils.inDungeon) return
            for (i in 0 until mc.thePlayer.inventory.sizeInventory){
                val itemStack = mc.thePlayer.inventory.getStackInSlot(i) ?: continue
                if (itemStack.displayName.stripColor().contains("Bonzo's Mask")){
                    hasBonzoInInv = true
                    bonzoMaskItem = itemStack
                    break
                }
                hasBonzoInInv = false
                bonzoMaskItem = null
            }
            if (!hasBonzoInInv) return
            if (bonzoMaskItem == null) return
            val timeNow = (System.currentTimeMillis()/1000).toDouble()
            val text = if (nextBonzoUse - timeNow < 0) "§aReady" else FontUtils.getTimeBetween(timeNow, nextBonzoUse)
            val textX = FontUtils.smartFontPlacement(16f, text.stripColor(), this)
            FontUtils.smartTexturePlacement(0f, this, "pauladdons/bonzoBasic.png", "pauladdons/bonzoMirror.png")
            fr.drawString(text, textX, 5f, 0xFFFFFF, true)

        }

        override fun demoRender() {
            val textX = FontUtils.smartFontPlacement(16f, "READY", this)
            FontUtils.smartTexturePlacement(0f, this, "pauladdons/bonzoBasic.png", "pauladdons/bonzoMirror.png")
            fr.drawString("§aREADY", textX, 5f, 0xFFFFFF, true)
        }

        override val toggled: Boolean
            get() = Config.bonzoMaskTimer
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
        nextBonzoUse = 0.0
    }
//Your ⚚ Bonzo's Mask saved your life!
    @SubscribeEvent
    fun onChat(event: ClientChatReceivedEvent){
        val message = event.message.unformattedText

        if (!Utils.inDungeon) return
        if (bonzoMaskItem == null) return
        if (!message.contains("Bonzo's Mask") || !message.contains("saved your life!")  || message.contains(":")) return
        val usedTime = (System.currentTimeMillis() / 1000).toDouble()
        var cooldownSeconds = 0
        for (line in Utils.getItemLore(bonzoMaskItem!!)){
            if (line == null) continue
            if (line.stripColor().startsWith("Cooldown: ")) cooldownSeconds = line.stripColor().replace(Regex("[^\\d]"),"").toInt()
        }
        logger.info("Got Bonzo Mask Cooldown: $cooldownSeconds")
        if (cooldownSeconds > 0) nextBonzoUse = usedTime + cooldownSeconds
    }
}