package me.atroxego.pauladdons.features.betterlootshare

import gg.essential.api.utils.Multithreading
import me.atroxego.pauladdons.config.Config
import me.atroxego.pauladdons.features.Feature
import me.atroxego.pauladdons.features.betterlootshare.ESP.logger
import me.atroxego.pauladdons.utils.Utils.getMobsForNotification
import me.atroxego.pauladdons.utils.Utils.stripColor
import net.minecraft.client.Minecraft
import net.minecraftforge.client.GuiIngameForge
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import org.lwjgl.opengl.GL11
import kotlin.math.pow

object MobNotification : Feature() {
    private val entitySeen = arrayListOf<Int>()
    var showNotification = false
    lateinit var notificationMob : MutableMap.MutableEntry<String,String>
//    private val entitySeen = mapOf<String,String>()
    @SubscribeEvent
    fun checkForMob(event: RenderWorldLastEvent){
        if (!Config.mobNotification){return}
        val world = Minecraft.getMinecraft().theWorld
        val entityList = world.loadedEntityList
        val mobsForNotification = getMobsForNotification()
//        logger.info(mobsForNotification)
        for (entity in entityList){
            if (entitySeen.contains(entity.entityId)) continue
            if (!entity.hasCustomName()){continue}
            for (mobForNotification in mobsForNotification){
                if (!entity.customNameTag.stripColor().contains(mobForNotification.key, true)) continue
                entitySeen.add(entity.entityId)
                displayNotification(mobForNotification.key)
                notificationMob = mobForNotification
                break
            }
        }

    }

    fun displayNotification(mobName: String) {
        val soundName = "random.orb"
        val volume = 1.0f
        var pitch = 1.0f

        Multithreading.runAsync {
            showNotification = true
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
            Thread.sleep(2800)
            showNotification = false
        }
    }

    @SubscribeEvent
    fun renderGameOverlay(event: RenderGameOverlayEvent.Post) {
        if (!showNotification) return
        if (Minecraft.getMinecraft().ingameGUI !is GuiIngameForge) return
        if (event.type != RenderGameOverlayEvent.ElementType.EXPERIENCE && event.type != RenderGameOverlayEvent.ElementType.JUMPBAR) return
        val fontRenderer = mc.fontRendererObj
//        notificationMob.value.plus("§l".plus(notificationMob.value))
        var text = notificationMob.value.plus("§l").plus(notificationMob.key)
        var altText = "§k§lIWONDERWHOANDWHYISREADINGTHAT"
//        var altText = "§k§lW"
//        var amountOfLetters = getStringWidth(text)*4 / getStringWidth("W")
//        do {
//            altText += "W"
//            amountOfLetters--
//        }while (amountOfLetters > 0)
//        logger.info(altText)
        var scale = 4.0 / (1080/mc.displayHeight)
        var scaleOther = 1.0 / (1080/mc.displayHeight)
        val scaleReset = scale.pow(-1.0)
        val scaleResetOther = scaleOther.pow(-1.0)
        val x = ((mc.displayWidth / (scale * 4)) - fontRenderer.getStringWidth(text) / 2).toFloat()
        val y = ((mc.displayHeight / (scale * 4)) - (17)).toFloat()
        val yTop = (y + (mc.displayHeight / (scaleOther * 4) * 0.5) - 3).toFloat()
        val yBot = (y + (mc.displayHeight / (scaleOther * 4) * 0.7)).toFloat()
        val altX = ((mc.displayWidth / (scaleOther * 4)) - fontRenderer.getStringWidth(altText) / 2).toFloat()
//        - 10 * fullScreen / current Screen
        logger.info(mc.displayWidth/2)
        logger.info(y)
        GL11.glScaled(scaleOther,scaleOther,scaleOther)
        fontRenderer.drawString(altText, altX, yTop, 0xFFFFFF, true)
        GL11.glScaled(scaleResetOther,scaleResetOther,scaleResetOther)
        GL11.glScaled(scale,scale,scale)
        fontRenderer.drawString(text, x, y, 0xFFFFFF, true)
        GL11.glScaled(scaleReset,scaleReset,scaleReset)
        GL11.glScaled(scaleOther,scaleOther,scaleOther)
        fontRenderer.drawString(altText, altX, yBot, 0xFFFFFF, true)
        GL11.glScaled(scaleResetOther,scaleResetOther,scaleResetOther)
    }


    @SubscribeEvent
    fun onWorldChange(event: WorldEvent.Load) {
        entitySeen.clear()
    }
}