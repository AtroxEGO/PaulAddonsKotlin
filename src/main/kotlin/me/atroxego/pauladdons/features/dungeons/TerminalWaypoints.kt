package me.atroxego.pauladdons.features.dungeons

import PaulAddons.Companion.mc
import gg.essential.universal.UMatrixStack
import me.atroxego.pauladdons.config.Config
import me.atroxego.pauladdons.render.RenderUtils.drawBeaconBeam
import me.atroxego.pauladdons.render.RenderUtils.renderWaypointText
import me.atroxego.pauladdons.utils.Utils
import me.atroxego.pauladdons.utils.Utils.stripColor
import net.minecraft.client.Minecraft
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.item.EntityArmorStand
import net.minecraft.util.AxisAlignedBB
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import kotlin.math.absoluteValue

object TerminalWaypoints {

//    @SubscribeEvent
//    fun onRenderMob(event: RenderLivingEvent.Pre<EntityLivingBase>) {
//        if (!Config.terminalWaypoints) return
//        if (!Utils.inDungeon) return
//        if (!event.entity.hasCustomName()) return
//        val terminals = listOf("Not Activated", "Inactive Terminal", "Inactive")
//        if (event.entity !is EntityArmorStand) return
//        if (!event.entity.hasCustomName()) return
//        when (event.entity.customNameTag.stripColor()) {
//            "Inactive" -> drawBeaconBeam(event.entity, Config.deviceBeaconColor.rgb, 1) // Device
//            "Not Activated" -> drawBeaconBeam(event.entity, Config.leverBeaconColor.rgb, 2) // Lever
//            "Inactive Terminal" -> drawBeaconBeam(event.entity, Config.terminalBeaconColor.rgb, 3) // Terminal
//            else -> return
//        }
//        if (Config.hideDefaultNames) {
//            for (near in getEntitiesInRadius(event.entity, 1)) {
//                if (near !is EntityArmorStand) continue
//                near.alwaysRenderNameTag = false
//            }
//        }
//    }

        @SubscribeEvent
        fun checkForMob(event: RenderWorldLastEvent){
            if (!Config.terminalWaypoints) return
            if (!Utils.inDungeon) return
            val world = Minecraft.getMinecraft().theWorld
            val entityList = world.loadedEntityList
            for (entity in entityList){
                if (!entity.hasCustomName()) continue
                if (entity !is EntityArmorStand) continue
                when (entity.customNameTag.stripColor()){
                    "Inactive" -> drawTerminalWaypoint(entity as EntityLivingBase, Config.deviceBeaconColor.rgb,1,event.partialTicks,"Device") // Device
                    "Not Activated" -> drawTerminalWaypoint(entity as EntityLivingBase, Config.leverBeaconColor.rgb,2,event.partialTicks,"Lever") // Lever
                    "Inactive Terminal" -> drawTerminalWaypoint(entity as EntityLivingBase, Config.terminalBeaconColor.rgb,3,event.partialTicks,"Terminal") // Terminal
                    else -> continue
                    }
                if(Config.hideDefaultNames){
                    for (near in getEntitiesInRadius(entity,1)){
                        if (near !is EntityArmorStand) continue
                        near.alwaysRenderNameTag = false
                    }
                }
            }
        }

        fun getEntitiesInRadius(center: Entity, radius: Int): List<Entity> {
            val world = center.worldObj
            val x = center.posX
            val y = center.posY
            val z = center.posZ
            return world.getEntitiesWithinAABB(
                Entity::class.java,
                AxisAlignedBB(x - radius, y - radius, z - radius, x + radius, y + radius, z + radius)
            )
        }

        fun drawTerminalWaypoint(
            entity: EntityLivingBase,
            color: Int,
            type: Int,
            partialTicks: Float,
            text: String
        ) {
            val diffX = (mc.thePlayer.posX - entity.posX).absoluteValue.toString()
            val diffZ = (mc.thePlayer.posZ - entity.posZ).absoluteValue.toString()
//            mc.thePlayer.addChatMessage(ChatComponentText("$diffX $diffZ"))
            if ((mc.thePlayer.posX - entity.posX).absoluteValue < 3 && (mc.thePlayer.posZ - entity.posZ).absoluteValue < 3) return
            drawBeaconBeam(entity, color, type)
            renderWaypointText(text, entity.posX, entity.posY + 3.5, entity.posZ, partialTicks, UMatrixStack())
        }
    }

