/*
 * Paul Addons - Hypixel Skyblock QOL Mod
 * Copyright (C) 2023  AtroxEGO
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */


package me.atroxego.pauladdons.features.funnyFishing

import PaulAddons.Companion.prefix
import gg.essential.api.utils.Multithreading
import me.atroxego.pauladdons.config.Config
import me.atroxego.pauladdons.events.impl.PacketEvent
import me.atroxego.pauladdons.features.Feature
import me.atroxego.pauladdons.features.other.InstaSell
import me.atroxego.pauladdons.features.other.PetSwapper
import me.atroxego.pauladdons.render.RenderUtils
import me.atroxego.pauladdons.utils.PlayerRotation
import me.atroxego.pauladdons.utils.Utils.VecToYawPitch
import me.atroxego.pauladdons.utils.Utils.addMessage
import me.atroxego.pauladdons.utils.Utils.blockPosToYawPitch
import me.atroxego.pauladdons.utils.Utils.findItemInHotbar
import me.atroxego.pauladdons.utils.Utils.fullInventory
import me.atroxego.pauladdons.utils.Utils.stripColor
import net.minecraft.client.settings.KeyBinding
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.projectile.EntityFishHook
import net.minecraft.init.Blocks
import net.minecraft.init.Items
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement
import net.minecraft.network.play.server.S14PacketEntity
import net.minecraft.network.play.server.S2APacketParticles
import net.minecraft.util.*
import net.minecraft.world.World
import net.minecraft.world.chunk.Chunk
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent
import org.lwjgl.input.Keyboard
import java.awt.Color


object FunnyFishing : Feature() {
    private var mainLookAtBlock: MovingObjectPosition? = null
    private var startingPosition: Vec3? = null
    private var rotateCooldown = 0L
    private var movementCooldown = 0L
    private var lastTimeHitEntity = 0L
    private var collidingEntity: EntityLivingBase? = null
    private var lastTimeTotemPlaced = 0L
    private var lastTimeReeled = 0L
    private var lastTimeSold = 0L

    fun toggleFishing() {
        if (!Config.funnyFishing) {
            mainLookAtBlock = mc.thePlayer.rayTrace(100.0, 1.0f)
            val rodSlot = getFishingRod()
            if (rodSlot == -1) {
                mc.thePlayer.addChatMessage(ChatComponentText("$prefix Haven't detected rod in hotbar"))
                return
            }
            mc.thePlayer.inventory.currentItem = rodSlot
            reelIn(false)
            startingPosition = mc.thePlayer.positionVector
//            startingPosition = Vec3(-361.5,63.0,266.5)
            rotateCooldown = System.currentTimeMillis()
            movementCooldown = System.currentTimeMillis()
            if (!BarnFishingTimer.timerRunning){
                BarnFishingTimer.timerRunning = true
                BarnFishingTimer.timerStartTime = System.currentTimeMillis()
            }
        } else {
            mainLookAtBlock = null
        }
        Config.funnyFishing = !Config.funnyFishing
        addMessage("$prefix Auto Fishing: ${if (Config.funnyFishing) "§aOn" else "§cOff"}")
    }

    private fun getFishingRod(): Int {
        for (i in 0..7) {
            val item = mc.thePlayer.inventory.mainInventory[i] ?: continue
            if (item.item == Items.fishing_rod) {
                return i
            }
        }
        return -1
    }

    private var playersFishHook: EntityFishHook? = null

    @SubscribeEvent
    fun onPacketRecive(event: PacketEvent.ReceiveEvent) {
        if (!Config.funnyFishing) return
        if (event.packet is S14PacketEntity.S17PacketEntityLookMove) {
            val entity = event.packet.getEntity(mc.theWorld)
            if (entity !is EntityFishHook || entity.angler != mc.thePlayer) return
            playersFishHook = entity
        }

        if (event.packet is S2APacketParticles) {
            if (playersFishHook == null) return
            if (event.packet.particleType != EnumParticleTypes.WATER_WAKE && event.packet.particleType != EnumParticleTypes.FLAME) return
            if (event.packet.particleCount != 6 || event.packet.particleSpeed != 0.2f) return
            val particlePosX = event.packet.xCoordinate
            val particlePosZ = event.packet.zCoordinate
            if (playersFishHook!!.getDistance(particlePosX, playersFishHook!!.posY, particlePosZ) < 0.1)
            // 0.05
            {
                printdev(
                    "Count: ${event.packet.particleCount} Speed: ${event.packet.particleSpeed} Distance: ${
                        playersFishHook!!.getDistance(
                            particlePosX,
                            playersFishHook!!.posY,
                            particlePosZ
                        )
                    }"
                )
                reelIn(true)

            }
        }
    }

    @SubscribeEvent
    fun onWorldLoad(event: WorldEvent.Load) {
        Config.funnyFishing = false
        rotateCooldown = 0
        lastTimeHitEntity = 0L
        lastTimeSold = 0L
        playersFishHook = null
        placingTotem = false
    }

    @SubscribeEvent
    fun onPlayerTick(event: PlayerTickEvent) {
        if (collidingEntity != null) {
            printdev("Colliding With: ${collidingEntity!!.name}")
            if (System.currentTimeMillis() / 1000 - lastTimeHitEntity > 2) {
                collidingEntity = null
                playersFishHook = null
                reelIn(true)
                lastTimeHitEntity = System.currentTimeMillis() / 1000
            }
        }
        if (!Config.funnyFishing) return
        if (placingTotem) return
        if (fullInventory() && System.currentTimeMillis() - lastTimeSold > 10000 && Config.funnyFishingAutoSell){
            mc.thePlayer.sendChatMessage("/bz")
            MinecraftForge.EVENT_BUS.register(InstaSell())
        }
        if (System.currentTimeMillis() - lastTimeReeled > 30000){
            reelIn(true)
        }
        if (mc.thePlayer.inventory.currentItem != getFishingRod() && !killing) {
            addMessage("$prefix Detected slot change, disabling")
            Config.funnyFishing = false
            playersFishHook = null
            return
        }
        if (playersFishHook != null){
            if (playersFishHook!!.onGround && playersFishHook!!.motionX == 0.0 && playersFishHook!!.motionZ == 0.0) {
                printdev("Should Recast")
                reelIn(true)
            }
        }

        if (Config.fishingTotem){
            if (!isTotemInRange() && System.currentTimeMillis() - lastTimeTotemPlaced > 10000) {
                printdev("Placing Totem")
                placeTotem()
                lastTimeTotemPlaced = System.currentTimeMillis()
            }
        }
        if (Config.fishingRotate){
            if (System.currentTimeMillis() - rotateCooldown >= 5000) {
                printdev("Rotating")
                printdev("Coords: ${mainLookAtBlock!!.blockPos.x}")
                val pitchOffset = ((Math.random() * (2.5 - -2.5)) + -2.5).toFloat()
                val yawOffset = ((Math.random() * (2.5 - -2.5)) + -2.5).toFloat()
                val yawAndPitch = blockPosToYawPitch(
                    BlockPos(mainLookAtBlock!!.blockPos.x + 1, mainLookAtBlock!!.blockPos.y, mainLookAtBlock!!.blockPos.z),
                    mc.thePlayer.positionVector
                )
                printdev("OffsetP: $pitchOffset OffsetY: $yawOffset")
                PlayerRotation(
                    PlayerRotation.Rotation(yawAndPitch.first + pitchOffset, yawAndPitch.second + yawOffset),
                    600L
                )
                rotateCooldown = System.currentTimeMillis()
            }
        }
        if (Config.fishingMove){
            if (System.currentTimeMillis() - movementCooldown >= 6000) {
                movePlayer()
                movementCooldown = System.currentTimeMillis()
            }
        }
    }

    private fun isTotemInRange() : Boolean{
        for (entity in mc.theWorld.loadedEntityList){
            if (mc.thePlayer.getDistanceToEntity(entity) > 10) continue
            if (!entity.hasCustomName()) continue
            if (!entity.customNameTag.stripColor().contains("Totem of Corruption")) continue
            return true
        }
        return false
    }
var placingTotem = false
     fun placeTotem(){
        val blockForTotem = getBlocksForTotem()
        val totemSlot = findItemInHotbar("Totem of Corruption")
        if (totemSlot == -1){
            addMessage("$prefix Haven't Found Totem in your hotbar")
            return
        }
        if (blockForTotem == null){
            addMessage("$prefix Haven't Found any proper blocks for totem")
            return
        }
        placingTotem = true
        Multithreading.runAsync{
            mc.thePlayer.inventory.currentItem = totemSlot
            var yawAndPitch = VecToYawPitch(
                Vec3(
                    if (blockForTotem.x > 0) blockForTotem.x - 0.5 else blockForTotem.x + 0.5,
                    blockForTotem.y.toDouble(),
                    if (blockForTotem.z > 0) blockForTotem.z - 0.5 else blockForTotem.z + 0.5
                ),
                mc.thePlayer.positionVector
            )
            PlayerRotation(
                PlayerRotation.Rotation(yawAndPitch.first, yawAndPitch.second),
                600L
            )
            Thread.sleep(500)
            val raytrace = mc.thePlayer.rayTrace(5.0,1.0f) ?: return@runAsync
            mc.netHandler.addToSendQueue(
                C08PacketPlayerBlockPlacement(
                    raytrace.blockPos,
                    1,mc.thePlayer.heldItem,
                    0.5F,1.0F,0.5F)
            )
            Thread.sleep(200)
            mc.thePlayer.inventory.currentItem = getFishingRod()
            yawAndPitch = blockPosToYawPitch(
                mainLookAtBlock!!.blockPos,
                mc.thePlayer.positionVector
            )
            PlayerRotation(
                PlayerRotation.Rotation(yawAndPitch.first, yawAndPitch.second),
                500L
            )
            Thread.sleep(200)
            mc.playerController.sendUseItem(mc.thePlayer,mc.theWorld,mc.thePlayer.heldItem)
            placingTotem = false
        }
    }

    fun getBlocksForTotem(): BlockPos? {
        for (offsetX in -2..2) {
            for (offsetZ in -2..2) {
            if (offsetX == 0 && offsetZ == 0) continue
                val blockPos = BlockPos(
                    mc.thePlayer.posX + offsetX,
                    mc.thePlayer.posY - 1,
                    mc.thePlayer.posZ + offsetZ
                )
                val blockAtBlockPos = mc.theWorld.getChunkFromBlockCoords(BlockPos(blockPos)).getBlock(BlockPos(blockPos))
                val blockOverBlockPos = mc.theWorld.getChunkFromBlockCoords(BlockPos(blockPos)).getBlock(BlockPos(blockPos.add(0,1,0)))
//                addMessage(blockAtBlockPos.registryName)
                if (blockAtBlockPos != Blocks.air && blockAtBlockPos != Blocks.water && blockAtBlockPos != Blocks.flowing_water && blockAtBlockPos != Blocks.lava && blockAtBlockPos != Blocks.flowing_lava && blockOverBlockPos == Blocks.air)
                {
                    printdev("Found Proper Block: ${blockPos} Block Type: ${blockAtBlockPos}")
                    return blockPos
                }
            }
        }
        return null
    }

    private fun movePlayer() {
        val moveStartedTime = System.currentTimeMillis()
        Multithreading.runAsync {
            if (startingPosition == null) {
                printdev("Starting Postion Is Null")
                return@runAsync
            }
            var decreaseXButton = 0
            var increaseXButton = 0
            var decreaseZButton = 0
            var increadeZButton = 0

            when(mc.thePlayer.horizontalFacing.name){
                "NORTH" -> {
                    decreaseXButton = Keyboard.KEY_A
                    increaseXButton = Keyboard.KEY_D
                    decreaseZButton = Keyboard.KEY_W
                    increadeZButton = Keyboard.KEY_S
                }
                "EAST" -> {
                    decreaseXButton = Keyboard.KEY_S
                    increaseXButton = Keyboard.KEY_W
                    decreaseZButton = Keyboard.KEY_A
                    increadeZButton = Keyboard.KEY_D
                }
                "SOUTH" -> {
                    decreaseXButton = Keyboard.KEY_D
                    increaseXButton = Keyboard.KEY_A
                    decreaseZButton = Keyboard.KEY_S
                    increadeZButton = Keyboard.KEY_W

                }
                "WEST" -> {
                    decreaseXButton = Keyboard.KEY_W
                    increaseXButton = Keyboard.KEY_S
                    decreaseZButton = Keyboard.KEY_D
                    increadeZButton = Keyboard.KEY_A
                }
            }


            KeyBinding.setKeyBindState(Keyboard.KEY_LSHIFT, true)
            if (mc.thePlayer.getDistance(
                    startingPosition!!.xCoord,
                    startingPosition!!.yCoord,
                    startingPosition!!.zCoord
                ) > 0.4
            ) {
                printdev("Coming Back to Main")
                while (mc.thePlayer.getDistance(startingPosition!!.xCoord, startingPosition!!.yCoord, startingPosition!!.zCoord) > 0.3 && System.currentTimeMillis() - moveStartedTime < 2000
                ) {
                    if (mc.thePlayer.positionVector.xCoord > startingPosition!!.xCoord) {
                        KeyBinding.setKeyBindState(decreaseXButton, true)
                    }
                    else if (mc.thePlayer.positionVector.xCoord < startingPosition!!.xCoord) KeyBinding.setKeyBindState(
                        increaseXButton,
                        true
                    )
                    if (mc.thePlayer.positionVector.zCoord > startingPosition!!.zCoord) KeyBinding.setKeyBindState(
                        decreaseZButton,
                        true
                    )
                    else if (mc.thePlayer.positionVector.zCoord < startingPosition!!.zCoord) KeyBinding.setKeyBindState(
                        increadeZButton,
                        true
                    )
                    if (!Config.funnyFishing) break
                }
            } else {
                printdev("Moving Randomly")
                val xOffset = ((Math.random() * (1 - -1)) + -1)
                val zOffset = ((Math.random() * (1 - -1)) + -1)
                printdev("New Location is: ${startingPosition!!.xCoord + xOffset} ${startingPosition!!.yCoord} ${startingPosition!!.zCoord + zOffset}")
                printdev(
                    "Distance: ${
                        mc.thePlayer.getDistance(
                            startingPosition!!.xCoord + xOffset,
                            startingPosition!!.yCoord,
                            startingPosition!!.zCoord + zOffset
                        )
                    }"
                )
                while (mc.thePlayer.getDistance(
                        startingPosition!!.xCoord + xOffset,
                        startingPosition!!.yCoord,
                        startingPosition!!.zCoord + zOffset
                    ) > 0.2 && System.currentTimeMillis() - moveStartedTime < 2000
                ) {
                    if (mc.thePlayer.positionVector.xCoord > startingPosition!!.xCoord + xOffset) KeyBinding.setKeyBindState(decreaseXButton, true)
                    else if (mc.thePlayer.positionVector.xCoord < startingPosition!!.xCoord + xOffset) KeyBinding.setKeyBindState(increaseXButton, true)
                    if (mc.thePlayer.positionVector.zCoord > startingPosition!!.zCoord + zOffset) KeyBinding.setKeyBindState(decreaseZButton, true)
                    else if (mc.thePlayer.positionVector.zCoord < startingPosition!!.zCoord + zOffset) KeyBinding.setKeyBindState(increadeZButton, true)
                    if (!Config.funnyFishing) break
                }
            }
            KeyBinding.setKeyBindState(Keyboard.KEY_W, false)
            KeyBinding.setKeyBindState(Keyboard.KEY_S, false)
            KeyBinding.setKeyBindState(Keyboard.KEY_A, false)
            KeyBinding.setKeyBindState(Keyboard.KEY_D, false)
            Thread.sleep((((Math.random() * (500 - 400)) + 400).toLong()))
            KeyBinding.setKeyBindState(Keyboard.KEY_LSHIFT, false)
            return@runAsync
        }
    }

    private fun getMobsWithinAABB(entity: Entity) {
        val aabb = AxisAlignedBB(entity.posX + 0.4, entity.posY - 2.0, entity.posZ + 0.4, entity.posX - 0.4, entity.posY + 0.2, entity.posZ - 0.4)
        val i = MathHelper.floor_double(aabb.minX - 1.0) shr 4
        val j = MathHelper.floor_double(aabb.maxX + 1.0) shr 4
        val k = MathHelper.floor_double(aabb.minZ - 1.0) shr 4
        val l = MathHelper.floor_double(aabb.maxZ + 1.0) shr 4
        for (i1 in i..j)
            for (j1 in k..l)
                this.getMobsWithinAABBForEntity(mc.theWorld.getChunkFromChunkCoords(i1, j1), entity, aabb)
    }


    private fun getMobsWithinAABBForEntity(chunk: Chunk, entityIn: Entity, aabb: AxisAlignedBB) {
        val entityLists = chunk.entityLists
        var i = MathHelper.floor_double((aabb.minY - World.MAX_ENTITY_RADIUS) / 16.0)
        var j = MathHelper.floor_double((aabb.maxY + World.MAX_ENTITY_RADIUS) / 16.0)
        i = MathHelper.clamp_int(i, 0, entityLists.size - 1)
        j = MathHelper.clamp_int(j, 0, entityLists.size - 1)
        for (k in i..j) {
            if (entityLists[k].isEmpty()) continue
            entity@ for (e in entityLists[k]) {
                if (!e.entityBoundingBox.intersectsWith(aabb.expand(0.7,0.7,0.7))) continue@entity
                if (e.name != "unknown") {
                    if(e.hasCustomName()) printdev(e.customNameTag)
                    else e.name
                }
                if (e !is EntityLivingBase) continue@entity
                if (e is EntityPlayer) continue@entity
                collidingEntity = e
                break
            }
        }
    }

    @SubscribeEvent
    fun onWorldRender(event: RenderWorldLastEvent) {
        if (!Config.funnyFishing) return
//        if (playersFishHook != null){
//            getMobsWithinAABB(playersFishHook!!)
//            if (collidingEntity != null){
////                printdev(collidingEntity!!.name)
//            }
//        }
        RenderUtils.drawFishingBox(mainLookAtBlock!!.blockPos, Color(236, 204, 8, 255), event.partialTicks)
    }

    private fun reelIn(recast: Boolean) {
        playersFishHook = null
        if (!BarnFishingTimer.timerRunning){
            BarnFishingTimer.timerRunning = true
            BarnFishingTimer.timerStartTime = System.currentTimeMillis()
        }
        lastTimeReeled = System.currentTimeMillis()
        Multithreading.runAsync {
            var randomCooldown = ((Math.random() * (100 - 50)) + 50).toLong()
            printdev("$randomCooldown")
            Thread.sleep(randomCooldown)
            mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.heldItem)
            if (!recast) return@runAsync
            randomCooldown = ((Math.random() * ((Config.fishingRecastDelay + 25) - (Config.fishingRecastDelay - 25))) + Config.fishingRecastDelay - 25).toLong()
            printdev("$randomCooldown")
            Thread.sleep(randomCooldown)
            mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.heldItem)
            collidingEntity = null
        }
    }

    private fun getFireVeil(): Int {
        for (i in 0..7) {
            val item = mc.thePlayer.inventory.mainInventory[i] ?: continue
            if (item.displayName.stripColor().contains("Fire Veil Wand")) {
                return i
            }
        }
        return -1
    }

    var killing = false

    @SubscribeEvent
    fun onChat(event: ClientChatReceivedEvent){
        if (!Config.funnyFishing) return
        if (!Config.fishingKilling) return
        for (mobMessage in FishingTracker.seaCreatureMessages){
            if (event.message.unformattedText.stripColor().lowercase() == mobMessage.key.lowercase()){
                killing = true
                when (getFireVeil()){
                    -1 -> addMessage("$prefix Haven't Found Fire Veil Wand!")
                    else -> {
                        Multithreading.runAsync{
                            Thread.sleep(100)
                            mc.thePlayer.inventory.currentItem = getFireVeil()
                            Thread.sleep(100)
                            mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.heldItem)
                            Thread.sleep(100)
                            mc.thePlayer.inventory.currentItem = getFishingRod()
                            Thread.sleep(100)
                            mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.heldItem)
                            killing = false
                        }
                    }
                }

            }
        }
    }
}