package me.atroxego.pauladdons.features.betterlootshare

import me.atroxego.pauladdons.config.Config
import me.atroxego.pauladdons.features.Feature
import me.atroxego.pauladdons.render.RenderUtils
import me.atroxego.pauladdons.utils.Utils.getMobsForNotification
import me.atroxego.pauladdons.utils.Utils.getRenderPartialTicks
import me.atroxego.pauladdons.utils.Utils.stripColor
import net.minecraft.client.model.ModelBase
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraftforge.client.event.RenderLivingEvent
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

object ESP : Feature() {
            val logger: Logger = LogManager.getLogger("PaulAddons")
            private val drawBox = hashMapOf<Entity, Int>()
        fun onRenderMob(mob: EntityLivingBase, model: ModelBase, event: RenderLivingEvent<EntityLivingBase>) {
            if(!Config.glowOnMob) return
            if (mob is EntityPlayer) {
                return;
            }
            if (!mob.hasCustomName()) return
            if (Config.espOnNotifiedMobs){
                val name = mob.customNameTag.stripColor()
                val mobsForESP = getMobsForNotification()
                for (mobName in mobsForESP){
                    if (!name.contains(mobName.key, true)) continue
                    drawEsp(
                        mob,
                        model,
                        Config.glowColor.rgb,
                        getRenderPartialTicks(),
                    )
                    return
                }
            }
            if (Config.customESPMobs.isEmpty()) return
            for (cname in Config.customESPMobs.split(", ")) {
                val name = mob.customNameTag.stripColor()
                if (!name.contains(cname, true)) continue
                drawEsp(
                    mob,
                    model,
                    Config.glowColor.rgb,
                    getRenderPartialTicks(),
                )
            }
        }

    private fun drawEsp(entity: EntityLivingBase, model: ModelBase, color: Int, partialTicks: Float) {
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
    }