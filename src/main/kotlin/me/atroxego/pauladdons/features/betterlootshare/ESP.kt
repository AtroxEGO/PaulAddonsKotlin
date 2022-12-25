package me.atroxego.pauladdons.features.betterlootshare

import gg.essential.api.utils.Multithreading
import me.atroxego.pauladdons.config.Config
import me.atroxego.pauladdons.features.Feature
import me.atroxego.pauladdons.utils.Utils.getRenderPartialTicks
import me.atroxego.pauladdons.render.RenderUtils
import net.minecraft.client.Minecraft
import net.minecraft.client.model.ModelBase
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraftforge.client.event.RenderLivingEvent
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.lang.Thread.sleep

object ESP : Feature() {
            val logger: Logger = LogManager.getLogger("PaulAddons")
            private val drawBox = hashMapOf<Entity, Int>()
            val entitySeen = arrayListOf<Int>()
        fun onRenderMob(mob: EntityLivingBase, model: ModelBase, event: RenderLivingEvent<EntityLivingBase>) {
            if(!Config.glowOnMob) return
            if (mob is EntityPlayer) {
                return;
            }
            if(mob.customNameTag != "Yeti"){return}
            drawEsp(
                mob,
                model,
                Config.glowColor.rgb,
                getRenderPartialTicks(),
            )
        }

    private fun drawEsp(entity: EntityLivingBase, model: ModelBase, color: Int, partialTicks: Float) {
        if(Config.mobNotification) checkForEntity(entity)
        when (Config.espSelector) {
            0 -> {
                RenderUtils.drawChamsEsp(entity, model, color, partialTicks)
            }
            1 -> {
//                drawBox[entity] = color
                RenderUtils.renderBoundingBox(entity, color)
            }
            2 -> {
                RenderUtils.drawOutlinedEsp(entity, model, color, partialTicks)
            }
        }
    }

        fun checkForEntity(entity: EntityLivingBase) {
            if (entitySeen.contains(entity.entityId)) {
                return
            }
            entitySeen.add(entity.entityId)
            logger.info(entitySeen)
            val titleText = "Yeti"
            val subtitleText = ""
            val soundName = "random.orb"
            val volume = 1.0f
            var pitch = 1.0f
            Minecraft.getMinecraft().ingameGUI.displayTitle(titleText, subtitleText, 10, 60, 10)
            Multithreading.runAsync {
                mc.thePlayer.playSound(soundName, volume, pitch)
                sleep(100)
                pitch = 1.5f
                mc.thePlayer.playSound(soundName, volume, pitch)
                sleep(100)
                pitch = 1.8f
                mc.thePlayer.playSound(soundName, volume, pitch)
            }
        }
    }