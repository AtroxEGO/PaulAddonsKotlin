package me.atroxego.pauladdons.features.funnyFishing

import PaulAddons.Companion.prefix
import gg.essential.api.utils.Multithreading
import me.atroxego.pauladdons.config.Config
import me.atroxego.pauladdons.events.impl.PacketEvent
import me.atroxego.pauladdons.features.Feature
import me.atroxego.pauladdons.render.RenderUtils
import me.atroxego.pauladdons.utils.PlayerRotation
import me.atroxego.pauladdons.utils.Utils.addMessage
import me.atroxego.pauladdons.utils.Utils.blockPosToYawPitch
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.projectile.EntityFishHook
import net.minecraft.init.Items
import net.minecraft.network.play.client.C09PacketHeldItemChange
import net.minecraft.network.play.server.S14PacketEntity
import net.minecraft.network.play.server.S2APacketParticles
import net.minecraft.util.*
import net.minecraft.world.World
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent
import java.awt.Color


object FunnyFishing : Feature() {
    var raytrace: MovingObjectPosition? = null

    fun toggleFishing(){
        if (!Config.funnyFishing){
            raytrace = mc.thePlayer.rayTrace(100.0,1.0f)
            val rodSlot = getFishingRod()
            if (rodSlot == -1){
                mc.thePlayer.addChatMessage(ChatComponentText("$prefix Haven't detected rod in hotbar"))
                return
            }
            mc.netHandler.addToSendQueue(C09PacketHeldItemChange(rodSlot))
            mc.thePlayer.inventory.currentItem = rodSlot
            mc.playerController.sendUseItem(mc.thePlayer,mc.theWorld,mc.thePlayer.heldItem)
        }else{
            raytrace = null
        }
        Config.funnyFishing = !Config.funnyFishing
        addMessage("$prefix Auto Fishing: ${if (Config.funnyFishing) "§aOn" else "§cOff"}")
        }
    fun getFishingRod(): Int {
        for (i in 0..7) {
            val item = mc.thePlayer.inventory.mainInventory[i] ?: continue
            if (item.item == Items.fishing_rod) {
                return i
            }
        }
        return -1
    }
    var playersFishHook : EntityFishHook? = null
    @SubscribeEvent
    fun onPacketRecive(event : PacketEvent.ReceiveEvent){
        if (!Config.funnyFishing) return
        if (event.packet is S14PacketEntity.S17PacketEntityLookMove){
            val entity = event.packet.getEntity(mc.theWorld)
            if (entity !is EntityFishHook || entity.angler != mc.thePlayer) return
            playersFishHook = entity
        }

        if (event.packet is S2APacketParticles){
            if (playersFishHook == null) return
            if (event.packet.particleType != EnumParticleTypes.WATER_WAKE) return
            if (event.packet.particleCount != 6 || event.packet.particleSpeed != 0.2f) return
            val particlePosX = event.packet.xCoordinate
            val particlePosZ = event.packet.zCoordinate
            if (playersFishHook!!.getDistance(particlePosX, playersFishHook!!.posY,particlePosZ) < 0.1)
            // 0.05
            {
                printdev("Count: ${event.packet.particleCount} Speed: ${event.packet.particleSpeed} Distance: ${playersFishHook!!.getDistance(particlePosX, playersFishHook!!.posY,particlePosZ)}")
                Multithreading.runAsync{
                    var randomCooldown = ((Math.random() * (100 - 50)) + 50).toLong()
                    printdev("$randomCooldown")
                    Thread.sleep(randomCooldown)
                    mc.playerController.sendUseItem(mc.thePlayer,mc.theWorld,mc.thePlayer.heldItem)
                    randomCooldown = ((Math.random() * (300 - 250)) + 250).toLong()
                    printdev("$randomCooldown")
                    Thread.sleep(randomCooldown)
                    mc.playerController.sendUseItem(mc.thePlayer,mc.theWorld,mc.thePlayer.heldItem)
                }
            }
        }
    }

//    var rotateCounter = 0
//    @SubscribeEvent
//    fun onPlayerTick(event : PlayerTickEvent){
//        if (mc.thePlayer.fishEntity != null){
//            val entityLists = mc.thePlayer.worldObj.getChunkFromChunkCoords(playersFishHook!!.chunkCoordX, playersFishHook!!.chunkCoordZ).entityLists
//            for (k in 0..entityLists.size) {
//                if (entityLists[k].isEmpty()) continue
//                for (e in entityLists[k]){
//                    if (!playersFishHook!!.entityBoundingBox.intersectsWith(e.entityBoundingBox)) continue
//                    printdev(e.name)
//                }
//            }
//        }

//        if (!Config.funnyFishing) return
//            rotateCounter++
//            if (rotateCounter >= 5000){
//                printdev("Rotating")
//                printdev("Coords: ${raytrace!!.blockPos.x}")
//                val yawAndPitch = blockPosToYawPitch(BlockPos(raytrace!!.blockPos.x + 1, raytrace!!.blockPos.y,raytrace!!.blockPos.z),mc.thePlayer.positionVector)
//                val pitchOffset = ((Math.random() * (5 - -5)) + -5).toFloat()
//                val yawOffset = ((Math.random() * (5 - -5)) + -5).toFloat()
//                printdev("OffsetP: $pitchOffset OffsetY: $yawOffset")
//                PlayerRotation(PlayerRotation.Rotation(yawAndPitch.first + pitchOffset, yawAndPitch.second + yawOffset), 600L)
//                rotateCounter = 0
//            }
//    }

//    @SubscribeEvent
//    fun onWorldRender(event: RenderWorldLastEvent){
//        if (!Config.funnyFishing) return
//        RenderUtils.drawFishingBox(raytrace!!.blockPos, Color(236, 204, 8, 255), event.partialTicks)
//    }
}