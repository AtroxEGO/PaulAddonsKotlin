package me.atroxego.pauladdons.features.funnyFishing

import PaulAddons
import PaulAddons.Companion.mc
import PaulAddons.Companion.prefix
import me.atroxego.pauladdons.config.Config
import me.atroxego.pauladdons.gui.GuiElement
import me.atroxego.pauladdons.render.DisplayNotification.displayNotification
import me.atroxego.pauladdons.render.font.FontUtils.getTimeBetween
import me.atroxego.pauladdons.render.font.FontUtils.smartFontPlacement
import me.atroxego.pauladdons.render.font.FontUtils.smartItemPlacement
import me.atroxego.pauladdons.utils.core.FloatPair
import net.minecraft.init.Items
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.ChatComponentText
import net.minecraftforge.event.entity.player.PlayerInteractEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

object BarnFishingTimer {
    init {
        BarnFishingTimerGUI()
    }
    var timerRunning = false
    var timerStartTime = 0L
    var notificationDisplayed = false
    class BarnFishingTimerGUI : GuiElement("Barn Fishing Timer", FloatPair(10, 10)) {
        private val fishingRod = ItemStack(Items.fishing_rod, 1, 1)
        override fun render() {
            val compound = fishingRod.tagCompound
            if (compound == null) {
                fishingRod.tagCompound = NBTTagCompound()
            }
            fishingRod.tagCompound.setString("Name", "Hellfire Rod")
            if(toggled){
                if (mc.thePlayer.heldItem == null) return
                if(mc.thePlayer.heldItem.item == Items.fishing_rod || timerRunning){
                    val text : String = if (timerRunning) getTimeBetween((timerStartTime/1000).toDouble(), (System.currentTimeMillis()/1000).toDouble())
                            else "0m0s"
                    smartItemPlacement(this,fishingRod)
                    fr.drawString(text, smartFontPlacement(16f,text,this), 6f, 0xFFFFFF, true)
//                    logger.info((timerStartTime - System.currentTimeMillis()) / 1000)
                    if ((System.currentTimeMillis() - timerStartTime) / 1000 >= Config.timestampOfBarnFishingNotification && !notificationDisplayed && timerRunning){
                        notificationDisplayed = true
                        displayNotification("§c${Config.barnFishingTimerText}", 3000,true)
                        }
                }
            }
        }

        override fun demoRender() {
            smartItemPlacement(this,fishingRod)
            fr.drawString("3m21s", smartFontPlacement(16f,"3m21s",this), 6f, 0xFFFFFF, true)
        }

        override val toggled: Boolean
            get() = Config.barnFishingTimer
//            get() = true
        override val height: Int
            get() = fr.FONT_HEIGHT + 3
        override val width: Int
            get() = 20 + fr.getStringWidth("3m21s")

        init {
            PaulAddons.guiManager.registerElement(this)
        }
    }
    @SubscribeEvent
    fun onRodCast(event: PlayerInteractEvent){
        if (!Config.barnFishingTimer) return
        if (event.entityPlayer.heldItem == null) return
        if (event.entityPlayer.heldItem.item == Items.fishing_rod && !timerRunning){
            timerRunning = true
            timerStartTime = System.currentTimeMillis()
            return
        }
        if (event.entityPlayer.heldItem.item != Items.fishing_rod && timerRunning){
            mc.thePlayer.addChatMessage(ChatComponentText("$prefix Detected right click on not a rod, disabling timer"))
            timerRunning = false
            timerStartTime = 0L
            notificationDisplayed = false
        }
    }
//    @SubscribeEvent
//    fun onRodCast(event: TickEvent.PlayerTickEvent){
//        if (mc.thePlayer.heldItem == null) return
//        if (mc.thePlayer.heldItem.item == Items.fishing_rod){
//            timerTicks++
//        }
//    }
}