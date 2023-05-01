package me.atroxego.pauladdons.render

import me.atroxego.pauladdons.PaulAddons.Companion.mc
import gg.essential.api.utils.Multithreading
import me.atroxego.pauladdons.PaulAddons
import me.atroxego.pauladdons.gui.GuiElement
import me.atroxego.pauladdons.utils.Utils.stripColor
import org.lwjgl.opengl.GL11
import java.awt.Color

object DisplayNotification {
    var renderNotification = false
    var notificationText = ""
    var displayTopAndBottomLines = false
    val altText = "§k§lIWONDERWHOANDWHYISREADINGTHAT"
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

    init {
        NotificationGUIElement()
    }

    class NotificationGUIElement : GuiElement("Notifications"){
        override fun render() {
            if (renderNotification){
                GL11.glPushMatrix()
                GL11.glScaled(4.0,4.0,4.0)
                GL11.glTranslated(230/8.0 - fr.getStringWidth(notificationText.stripColor()).toDouble() / 2,0.0,0.0)
                fr.drawString(notificationText, 0f, 4f, color.rgb, true)
                GL11.glPopMatrix()
                if (displayTopAndBottomLines){
                    fr.drawString(altText, 17f, 0f, 0xFFFFFF, true)
                    fr.drawString(altText, 17f, 60f, 0xFFFFFF, true)
                }
            }
        }

        override fun demoRender() {
            val text = "§4Notification"
            fr.drawString(altText, 17f, 0f, 0xFFFFFF, true)
            GL11.glPushMatrix()
            GL11.glScaled(4.0,4.0,4.0)
            GL11.glTranslated(230/8.0 - fr.getStringWidth(text.stripColor()).toDouble() / 2,0.0,0.0)
            fr.drawString(text, 0f, 4f, color.rgb, true)
            GL11.glPopMatrix()
            fr.drawString(altText, 17f, 60f, 0xFFFFFF, true)
        }

        override val toggled: Boolean
            get() = true
        override val height: Int
            get() = 60
        override val width: Int
            get() = 230

        init {
            PaulAddons.guiManager.registerElement(this)
        }

    }
}