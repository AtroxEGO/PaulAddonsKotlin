package me.atroxego.pauladdons.render

import PaulAddons.Companion.mc
import gg.essential.api.utils.Multithreading
import net.minecraft.client.Minecraft
import net.minecraftforge.client.GuiIngameForge
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import org.lwjgl.opengl.GL11
import java.awt.Color
import kotlin.math.pow

object DisplayNotification {
    var renderNotification = false
    var notificationText = ""
    var displayTopAndBottomLines = false
    var color: Color = Color.WHITE
    fun displayNotification(text:String,duration:Long,topAndBottomLines:Boolean) {
        val soundName = "random.orb"
        val volume = 1.0f
        var pitch = 1.0f
        notificationText = text
        displayTopAndBottomLines = topAndBottomLines
        Multithreading.runAsync {
            renderNotification = true
            mc.thePlayer.playSound(soundName, volume, pitch)
            Thread.sleep(100)
            pitch = 1.5f
            mc.thePlayer.playSound(soundName, volume, pitch)
            Thread.sleep(100)
            pitch = 1.8f
            mc.thePlayer.playSound(soundName, volume, pitch)
            Thread.sleep(100)
            pitch = 1.2f
            mc.thePlayer.playSound(soundName, volume, pitch)
            Thread.sleep(100)
            pitch = 1.0f
            mc.thePlayer.playSound(soundName, volume, pitch)
            Thread.sleep(100)
            pitch = 2.0f
            mc.thePlayer.playSound(soundName, volume, pitch)
            Thread.sleep(duration)
            renderNotification = false
        }
    }
    @SubscribeEvent
    fun renderGameOverlay(event: RenderGameOverlayEvent.Post) {
        if (!renderNotification) return
        if (Minecraft.getMinecraft().ingameGUI !is GuiIngameForge) return
        if (event.type != RenderGameOverlayEvent.ElementType.EXPERIENCE && event.type != RenderGameOverlayEvent.ElementType.JUMPBAR) return
        val fontRenderer = mc.fontRendererObj
        val text = notificationText
//        var altText = "??k??lW"
//        var amountOfLetters = getStringWidth(text)*4 / getStringWidth("W")
//        do {
//            altText += "W"
//            amountOfLetters--
//        }while (amountOfLetters > 0)
//        logger.info(altText)
        val scale = 4.0 / (1080/ mc.displayHeight)
        val scaleReset = scale.pow(-1.0)
        val x = (mc.displayWidth / (scale * 4) - fontRenderer.getStringWidth(text) / 2).toFloat()
        val y = (mc.displayHeight / (scale * 4) - 17).toFloat()
        GL11.glScaled(scale,scale,scale)
        fontRenderer.drawString(text, x, y, color.rgb, true)
        GL11.glScaled(scaleReset,scaleReset,scaleReset)

        if (displayTopAndBottomLines){
            val altText = "??k??lIWONDERWHOANDWHYISREADINGTHAT"
            val scaleOther = 1.0 / (1080/ mc.displayHeight)
            val scaleResetOther = scaleOther.pow(-1.0)
            val yTop = (y + mc.displayHeight / (scaleOther * 4) * 0.5 - 3).toFloat()
            val yBot = (y + mc.displayHeight / (scaleOther * 4) * 0.7).toFloat()
            val altX = (mc.displayWidth / (scaleOther * 4) - fontRenderer.getStringWidth(altText) / 2).toFloat()
            GL11.glScaled(scaleOther,scaleOther,scaleOther)
            fontRenderer.drawString(altText, altX, yTop, 0xFFFFFF, true)
            fontRenderer.drawString(altText, altX, yBot, 0xFFFFFF, true)
            GL11.glScaled(scaleResetOther,scaleResetOther,scaleResetOther)
        } else return
//        MobNotification.notificationMob.value.plus("??l").plus(MobNotification.notificationMob.key)
    }
}