package me.atroxego.pauladdons.features.dungeons

import PaulAddons.Companion.mc
import gg.essential.universal.UScreen
import me.atroxego.pauladdons.config.Config
import me.atroxego.pauladdons.features.betterlootshare.ESP.logger
import me.atroxego.pauladdons.features.betterlootshare.MobNotification
import me.atroxego.pauladdons.render.DisplayNotification
import me.atroxego.pauladdons.render.RenderUtils.drawBeaconBeam
import me.atroxego.pauladdons.utils.Utils
import me.atroxego.pauladdons.utils.Utils.stripColor
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.WorldRenderer
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.item.EntityArmorStand
import net.minecraft.util.ChatComponentText
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.event.RenderLivingEvent
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import org.lwjgl.util.Color

object TerminalWaypoints {

    @SubscribeEvent
//    fun onRenderMob(event: RenderLivingEvent.Pre<EntityLivingBase>) {
//        if (!Config.terminalWaypoints) return
//        if (!Utils.inDungeon) return
//        if (!event.entity.hasCustomName()) return
//        val terminals = listOf("Not Activated","Inactive Terminal","Inactive")
//        if (event.entity !is EntityArmorStand) return
//            for (i in 0..2){
//                if (terminals[i] == event.entity.customNameTag.stripColor()){
//                    when (event.entity.customNameTag){
//                        "Inactive" -> drawBeaconBeam(event.entity, Config.deviceBeaconColor.rgb, 1) // Device
//                        "Not Activated" -> drawBeaconBeam(event.entity, Config.leverBeaconColor.rgb, 2) // Lever
//                        "Inactive Terminal" -> drawBeaconBeam(event.entity, Config.terminalBeaconColor.rgb, 3) // Terminal
//                    }
//                }
//            }

        fun checkForMob(event: RenderWorldLastEvent){
            if (UScreen.currentScreen.toString().contains("gg.essential.vigilance.gui.SettingsGui")) return
            if (!Config.terminalWaypoints) return
            val world = Minecraft.getMinecraft().theWorld
            val entityList = world.loadedEntityList
            for (entity in entityList){
                if (!entity.hasCustomName()){continue}
                when (entity.customNameTag){
                    "Inactive" -> drawBeaconBeam(entity as EntityLivingBase, Config.deviceBeaconColor.rgb, 1) // Device
                    "Not Activated" -> drawBeaconBeam(entity as EntityLivingBase, Config.leverBeaconColor.rgb, 2) // Lever
                    "Inactive Terminal" -> drawBeaconBeam(entity as EntityLivingBase, Config.terminalBeaconColor.rgb, 3) // Terminal
                    }
            }
        }
    }

