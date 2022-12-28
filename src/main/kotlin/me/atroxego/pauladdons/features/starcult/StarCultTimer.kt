package me.atroxego.pauladdons.features.starcult

import PaulAddons
import me.atroxego.pauladdons.features.Feature
import me.atroxego.pauladdons.utils.ApiDateInformation.getDateInformation
import net.minecraft.client.Minecraft
import net.minecraft.util.ChatComponentText
import net.minecraft.util.EnumChatFormatting
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import kotlin.math.floor

object StarCultTimer : Feature() {
        var text = "A"
        var basicScale = 3.0

//    @SubscribeEvent
//    fun renderGameOverlay(event: RenderGameOverlayEvent.Post) {
//        if (Minecraft.getMinecraft().ingameGUI !is GuiIngameForge) return
//        if (Minecraft.getMinecraft().currentScreen is LocationEditGui) return
//        if (event.type != RenderGameOverlayEvent.ElementType.EXPERIENCE && event.type != RenderGameOverlayEvent.ElementType.JUMPBAR) return
//        val fontRenderer = mc.fontRendererObj
//        val timeNow = (System.currentTimeMillis() / 1000).toDouble()
//        val helmetIcon = ResourceLocation("pauladdons/helmet.png")
//        text = if (getTimeSecBetween(timeNow, nextCult) < 0 || cultActive) {
//            "§a" + getTimeCultEnd(timeNow, nextCult)
//        } else {
//            getTimeBetween(timeNow, nextCult)
//        }
//        val scale = basicScale / (1920 / mc.displayWidth)
//        val scaleReset = scale.pow(-1.0)
//        val x = smartFontPlacement(Config.starCultTimerX.toFloat(), scale, text)
////        val x = (((Config.starCultTimerX + ((16*scale)* (1920/ mc.displayWidth))) / scale)/ (1920/ mc.displayWidth)).toFloat()
//        val y = (((Config.starCultTimerY + ((4*scale)* (1080/ mc.displayHeight))) / scale)/ (1080/ mc.displayHeight)).toFloat()
//        GL11.glScaled(scale,scale,scale)
//        fontRenderer.drawString(text, x, y, 0xFFFFFF, true)
//        renderTexture(helmetIcon, ((Config.starCultTimerX / scale) / (1920/ mc.displayWidth)).toInt(),((Config.starCultTimerY / scale)/ (1080/ mc.displayHeight)).toInt(), 16,16)
//        GL11.glScaled(scaleReset,scaleReset,scaleReset)
//    }

//    fun smartFontPlacement(position : Float, scale: Double, text: String) :Float{
//        var newPosition = 0F
////        (mc.displayWidth / (4*scale))
//        if(position < 400){
//            newPosition = (((position + ((16*scale)* (1920/ mc.displayWidth))) / scale)/ (1920/ mc.displayWidth)).toFloat()
//        }else{
//            newPosition = (((position - ((mc.fontRendererObj.getStringWidth(text)*scale)* (1920/ mc.displayWidth))) / scale)/ (1920/ mc.displayWidth)).toFloat()
//        }
//        return newPosition
//    }

    @SubscribeEvent
    fun onWorldChange(event: WorldEvent.Load){
        getDateInformation()
    }

    var veryImportantBoolean = false
    fun getTimeSecBetween (timeOne: Double,timeTwo: Double): Double{
        if ((timeTwo - timeOne < 0 && timeTwo - timeOne > -1) && !veryImportantBoolean) {
            Minecraft.getMinecraft().thePlayer.addChatMessage(ChatComponentText(PaulAddons.prefix + EnumChatFormatting.DARK_AQUA + " Star Cult Active!"));
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
//    var cultActive = false
//    var secondsTillCult = 0.0
//    var nextCult = 0.0
//    fun getNextCult() {
//        val timeNow = (System.currentTimeMillis() / 1000).toDouble()
//        var nextCultInDays = 0
//        var nextCultInHours = 0
//        var nextCultInMinutes = 0
//        if (currentDay % 7 == 0 && currentHour < 6) {
//            cultActive = true
//            nextCultInDays = -1
//            nextCultInHours = 23 - currentHour
//            nextCultInMinutes = 60 - currentMinute
//        } else {
//            var i = 28
//            while (i >= 7) {
//                if (currentDay < i) {
//                    cultActive = false
//                    nextCultInDays = i - 1 - currentDay
//                    nextCultInHours = 23 - currentHour
//                    nextCultInMinutes = 60 - currentMinute
//                }
//                i -= 7
//            }
//        }
//        if (currentDay >= 28 && currentHour >= 6 || currentDay > 28) {
//            cultActive = false
//            nextCultInDays = 37 - currentDay
//            nextCultInHours = 23 - currentHour
//            nextCultInMinutes = 60 - currentMinute
//        }
//        secondsTillCult = nextCultInDays * 1200 + nextCultInHours * 50 + (nextCultInMinutes / 10 ) * 8.3333333
//        nextCult = timeNow + secondsTillCult
//        logger.info(nextCult)
//    }
//
//    fun getTimeCultEnd(timeOne: Double, timeTwo: Double): String {
//        if ((currentDay + 1) % 7 == 0) {
//            currentDay++
//            currentHour = 0
//            currentMinute = 0
//        }
//        return if (timeTwo + 300 - timeOne < 0) {
//            getDateInformation()
//            getNextCult()
//            veryImportantBoolean = false
//            "What Happened? API prob down -_-"
//        } else getTimeBetween(timeOne, timeTwo + 300)
//    }
}
