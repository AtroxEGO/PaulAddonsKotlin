package me.atroxego.pauladdons.features.starcult

import PaulAddons
import PaulAddons.Companion.mc
import me.atroxego.pauladdons.config.Cache
import me.atroxego.pauladdons.config.Config
import me.atroxego.pauladdons.gui.GuiElement
import me.atroxego.pauladdons.render.DisplayNotification.displayNotification
import me.atroxego.pauladdons.render.font.FontUtils.getTimeBetween
import me.atroxego.pauladdons.render.font.FontUtils.smartFontPlacement
import me.atroxego.pauladdons.render.font.FontUtils.smartTexturePlacement
import me.atroxego.pauladdons.utils.ApiDateInformation
import me.atroxego.pauladdons.utils.ApiDateInformation.getDateInformation
import me.atroxego.pauladdons.utils.Utils.stripColor
import me.atroxego.pauladdons.utils.core.FloatPair
import net.minecraft.util.ChatComponentText
import net.minecraft.util.EnumChatFormatting
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

object StarCult {

    init {
        StarCultTimerGuiElement()
    }
    class StarCultTimerGuiElement : GuiElement("Star Cult Timer", FloatPair(10, 10)) {
        override fun render() {
            var text = ""
            if (toggled) {
                val timeNow = (System.currentTimeMillis() / 1000).toDouble()
                text = if (ApiDateInformation.busy) {
                    "Wait"
                } else if (getTimeSecBetween(timeNow, nextCult) < 0 || cultActive) {
                    "§a" + getTimeCultEnd(timeNow, nextCult)
                } else {
                    getTimeBetween(timeNow, nextCult)
                }
                val textX = smartFontPlacement(16f, text.stripColor(), this)
                smartTexturePlacement(0f, this, "pauladdons/helmetBasic.png", "pauladdons/helmetMirror.png")
                fr.drawString(text, textX, 5f, 0xFFFFFF, true)
            }
        }

        override fun demoRender() {
            val textX = smartFontPlacement(16f, "1h59m", this)
            smartTexturePlacement(0f, this, "pauladdons/helmetBasic.png", "pauladdons/helmetMirror.png")
            fr.drawString("1h59m", textX, 5f, 0xFFFFFF, true)
        }

        override val height: Int
            get() = fr.FONT_HEIGHT
        override val width: Int
            get() = 20 + fr.getStringWidth("1h59m")

        override val toggled: Boolean
            get() = Config.starCultTimer

        init {
            PaulAddons.guiManager.registerElement(this)
        }
    }

    @SubscribeEvent
    fun onWorldChange(event: WorldEvent.Load) {
    if (!cultActive){
        getDateInformation()
        getNextCult()
        }
    }

    var veryImportantBoolean = false
    fun getTimeSecBetween(timeOne: Double, timeTwo: Double): Double {
        if (timeTwo - timeOne < 0 && timeTwo - timeOne > -1 && !veryImportantBoolean && Cache.currentDay != 0) {
            if (Config.cStarCultNotification) mc.thePlayer.addChatMessage(ChatComponentText(PaulAddons.prefix + EnumChatFormatting.DARK_AQUA + " Star Cult Active!"))
            if (Config.gcStarCultNofification) {
                val customMessage = Config.starCultGuildMessage
                if (customMessage != "") {
                    mc.thePlayer.sendChatMessage("/gc $customMessage")
                } else mc.thePlayer.addChatMessage(ChatComponentText(PaulAddons.prefix + "Star Cult Guild Message Is Empty"))
            }
            if (Config.screenStarCultNotification) displayNotification("§9§lStar Cult", 3000, true)
            veryImportantBoolean = true
        }
        return timeTwo - timeOne
    }

    var cultActive = false
    var secondsTillCult = 0.0
    var nextCult = 0.0
    fun getNextCult() {
        val timeNow = (System.currentTimeMillis() / 1000).toDouble()
        var nextCultInDays = 0
        var nextCultInHours = 0
        var nextCultInMinutes = 0
        if (Cache.currentDay % 7 == 0 && Cache.currentHour < 6 && Cache.currentDay != 0 ) {
            cultActive = true
            nextCultInDays = -1
            nextCultInHours = 23 - Cache.currentHour
            nextCultInMinutes = 60 - Cache.currentMinute
        } else {
            var i = 28
            while (i >= 7) {
                if (Cache.currentDay < i) {
                    cultActive = false
                    nextCultInDays = i - 1 - Cache.currentDay
                    nextCultInHours = 23 - Cache.currentHour
                    nextCultInMinutes = 60 - Cache.currentMinute
                }
                i -= 7
            }
        }
        if (Cache.currentDay >= 28 && Cache.currentHour >= 6 || Cache.currentDay > 28) {
            cultActive = false
            nextCultInDays = 37 - Cache.currentDay
            nextCultInHours = 23 - Cache.currentHour
            nextCultInMinutes = 60 - Cache.currentMinute
        }
        secondsTillCult = nextCultInDays * 1200 + nextCultInHours * 50 + nextCultInMinutes * 0.83333333
        nextCult = timeNow + secondsTillCult
    }

    fun getTimeCultEnd(timeOne: Double, timeTwo: Double): String {
//        if ((Cache.currentDay + 1) % 7 == 0) {
//            Cache.currentDay++
//            Cache.currentHour = 0
//            Cache.currentMinute = 0
//        }
        if (timeTwo + 300 - timeOne < 0 && timeOne != 0.0 && timeTwo != 0.0) {
            getDateInformation()
            getNextCult()
            return "What Happened?"
        } else return getTimeBetween(timeOne, timeTwo + 300)
    }
}