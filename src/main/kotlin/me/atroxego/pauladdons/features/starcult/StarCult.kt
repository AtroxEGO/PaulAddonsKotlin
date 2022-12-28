package me.atroxego.pauladdons.features.starcult

import PaulAddons
import PaulAddons.Companion.mc
import me.atroxego.pauladdons.config.Cache
import me.atroxego.pauladdons.config.Config
import me.atroxego.pauladdons.features.betterlootshare.ESP
import me.atroxego.pauladdons.features.betterlootshare.MobNotification
import me.atroxego.pauladdons.gui.GuiElement
import me.atroxego.pauladdons.render.RenderUtils.renderTexture
import me.atroxego.pauladdons.utils.ApiDateInformation
import me.atroxego.pauladdons.utils.core.FloatPair
import net.minecraft.client.Minecraft
import net.minecraft.util.ChatComponentText
import net.minecraft.util.EnumChatFormatting
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.GuiIngameForge
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import org.lwjgl.opengl.GL11
import kotlin.math.floor
import kotlin.math.pow
import kotlin.time.ExperimentalTime

object StarCult {

    init {
        StarCultTimerGuiElement()
    }
    class StarCultTimerGuiElement : GuiElement("Star Cult Timer", FloatPair(10, 10)) {

        @OptIn(ExperimentalTime::class)
        override fun render() {
            var text = ""
            if (toggled) {
                val timeNow = (System.currentTimeMillis() / 1000).toDouble()
        text = if (getTimeSecBetween(timeNow, nextCult) < 0 || cultActive) {
            "§a" + getTimeCultEnd(timeNow, nextCult)
        } else {
            getTimeBetween(timeNow, nextCult)
        }
//                var texture = ResourceLocation("textures/items/apple.png")
//                renderTexture(texture,0,0)
                val textX = smartFontPlacement(16f,text)
                smartTexturePlacement(0f, text)
                fr.drawString(text, textX, 5f, 0xFFFFFF, true)
//                renderTexture(helmetIcon, textureX,0)
            }
        }

        override fun demoRender() {
            val textX = smartFontPlacement(16f,"1h59m")
            smartTexturePlacement(0f, "1h59m")
            fr.drawString("1h59m", textX, 5f, 0xFFFFFF, true)
//            renderTexture(helmetIcon, textureX,0, 16,16)
//            logger.info()
        }

        private fun smartFontPlacement(position : Float, text: String) :Float{
            if(this.actualX < mc.displayWidth/4){
               return position
            }else{
                var offset = 0f
                when (text.length){
                    4 -> offset = 6f
                    5 -> offset = 12f
                    6 -> offset = 18f
                }
                return position - offset
            }

        }

        private fun smartTexturePlacement(position : Float, text: String){
             if(this.actualX < mc.displayWidth/4){
                 var textureBasic = ResourceLocation("pauladdons/helmetBasic.png")
                 renderTexture(textureBasic, position.toInt(),0)
            }else{
                 var textureMirrored = ResourceLocation("pauladdons/helmetMirror.png")
                renderTexture(textureMirrored, (position+34).toInt(),0)
            }
        }

        override val height: Int
            get() = fr.FONT_HEIGHT
        override val width: Int
            get() = 20 + fr.getStringWidth("1h59m")

        override val toggled: Boolean
            get() = true

        init {
            PaulAddons.guiManager.registerElement(this)
        }
    }

    @SubscribeEvent
    fun onWorldChange(event: WorldEvent.Load){
        ApiDateInformation.getDateInformation()
    }


    private var veryImportantBoolean = false
    fun getTimeSecBetween (timeOne: Double,timeTwo: Double): Double{
        if ((timeTwo - timeOne < 0 && timeTwo - timeOne > -1) && !veryImportantBoolean) {
            Minecraft.getMinecraft().thePlayer.addChatMessage(ChatComponentText(PaulAddons.prefix + EnumChatFormatting.DARK_AQUA + " Star Cult Active!"));
            if (Config.gcStarCultNofification) mc.thePlayer.sendChatMessage("/gc [Paul Addons] Star Cult Active!")
            veryImportantBoolean = true;
        }
        return timeTwo - timeOne;
    }

    fun getTimeBetween(timeOne: Double, timeTwo: Double): String {
        val secondsBetween = floor(timeTwo - timeOne)
        val timeFormatted: String
        val days: Int
        val hours: Int
        val minutes: Int
        var seconds = 0
        if (secondsBetween > 86400) {
            // More than 1d, display #d#h
            days = (secondsBetween / 86400).toInt()
            hours = (secondsBetween % 86400 / 3600).toInt()
            timeFormatted = days.toString() + "d" + hours + "h"
        } else if (secondsBetween > 3600) {
            // More than 1h, display #h#m
            hours = (secondsBetween / 3600).toInt()
            minutes = (secondsBetween % 3600 / 60).toInt()
            timeFormatted = hours.toString() + "h" + minutes + "m"
        } else {
            // Display #m#s
            minutes = (secondsBetween / 60).toInt()
            seconds = (secondsBetween % 60).toInt()
            timeFormatted = minutes.toString() + "m" + seconds + "s"
        }
        return timeFormatted
    }
    var cultActive = false
    var secondsTillCult = 0.0
    var nextCult = 0.0
    fun getNextCult() {
        val timeNow = (System.currentTimeMillis() / 1000).toDouble()
        var nextCultInDays = 0
        var nextCultInHours = 0
        var nextCultInMinutes = 0
        if (Cache.currentDay % 7 == 0 && Cache.currentHour < 6) {
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
        if ((Cache.currentDay + 1) % 7 == 0) {
            Cache.currentDay++
            Cache.currentHour = 0
            Cache.currentMinute = 0
        }
        return if (timeTwo + 300 - timeOne < 0) {
            ApiDateInformation.getDateInformation()
            getNextCult()
            veryImportantBoolean = false
            "What Happened? API prob down -_-"
        } else getTimeBetween(timeOne, timeTwo + 300)
    }

}