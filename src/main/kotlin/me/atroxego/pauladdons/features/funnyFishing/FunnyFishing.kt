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

import me.atroxego.pauladdons.PaulAddons.Companion.prefix
import gg.essential.api.utils.Multithreading
import gg.essential.universal.UChat
import me.atroxego.pauladdons.config.Config
import me.atroxego.pauladdons.config.Config.funnyFishingAutoHook
import me.atroxego.pauladdons.events.impl.PacketEvent
import me.atroxego.pauladdons.features.Feature
import me.atroxego.pauladdons.features.other.InstaSell
import me.atroxego.pauladdons.features.other.WardrobeEquipper
import me.atroxego.pauladdons.render.RenderUtils
import me.atroxego.pauladdons.utils.PlayerRotation
import me.atroxego.pauladdons.utils.SBInfo
import me.atroxego.pauladdons.utils.Utils.VecToYawPitch
import me.atroxego.pauladdons.utils.Utils.addMessage
import me.atroxego.pauladdons.utils.Utils.blockPosToYawPitch
import me.atroxego.pauladdons.utils.Utils.findItemInHotbar
import me.atroxego.pauladdons.utils.Utils.fullInventory
import me.atroxego.pauladdons.utils.Utils.stripColor
import me.atroxego.pauladdons.utils.Utils.switchToItemInHotbar
import net.minecraft.block.BlockDynamicLiquid
import net.minecraft.block.BlockStaticLiquid
import net.minecraft.client.settings.KeyBinding
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.item.EntityArmorStand
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
    var mainLookAtBlock: MovingObjectPosition? = null
    var startingPosition: Vec3? = null
    private var rotateCooldown = 0L
    private var movementCooldown = 0L
    private var lastTimeHitEntity = 0L
    private var collidingEntity: EntityLivingBase? = null
    private var lastTimeTotemPlaced = 0L
    private var lastTimeReeled = 0L
    private var lastTimeSold = 0L
    private var rodSlotIndex = -1
    private var lookForGolenFish = false
    private var goldenFishEntity : EntityArmorStand? = null
    private var seenFlames = mutableListOf<Int>()
    private var enemyEntity: EntityArmorStand? = null
    private var lastGoldenFishLookTime = 0L
    var pauseMacro = false

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

    fun getFishingRod(): Int {
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
                if(Config.specialTropyMode == 1){
                    reelIn(false)
                    val starterRodSlotIndex = findItemInHotbar("Starter Lava Rod")
                    if (starterRodSlotIndex == -1){
                        UChat.chat("$prefix Haven't found Starter Lava Rod, make sure it's in the hotbar")
                        reelIn(false)
                    } else {
                        Multithreading.runAsync {
                            var normalRodSlot = -1
                            if (findItemInHotbar("Magma Rod") == -1){
                                if (findItemInHotbar("Inferno Rod") == -1){
                                    if (findItemInHotbar("Hellfire Rod") == -1) {
                                        UChat.chat("$prefix Haven't found Lava Rod that's not Starter Lava Rod in your hotbar")
                                    } else normalRodSlot = findItemInHotbar("Hellfire Rod")
                                }else normalRodSlot = findItemInHotbar("Inferno Rod")
                            } else normalRodSlot = findItemInHotbar("Magma Rod")
                            Thread.sleep(300)
                            switchToItemInHotbar(normalRodSlot)
                            Thread.sleep(1500)
                            reelIn(false)
                            Thread.sleep(1500)
                            switchToItemInHotbar(findItemInHotbar("Starter Lava Rod"))
                        }
                    }
                } else if (Config.specialTropyMode == 2) {
                    reelIn(false)
                    Multithreading.runAsync {
                        mc.thePlayer.sendChatMessage("/wardrobe")
                        MinecraftForge.EVENT_BUS.register(WardrobeEquipper("Ember Helmet"))
                        Thread.sleep(2000)
                        reelIn(false)
                        Thread.sleep(20000)
                        if (Config.funnyFishing){
                            mc.thePlayer.sendChatMessage("/wardrobe")
                            MinecraftForge.EVENT_BUS.register(WardrobeEquipper("Hunter"))
                        }
                    }


                } else reelIn(true)
            }
        }
    }

    @SubscribeEvent
    fun onWorldLoad(event: WorldEvent.Load) {
        if (Config.funnyFishing && !pauseMacro){
            UChat.chat("$prefix Pausing Fishing Macro")
            pauseMacro = true
        }
        rotateCooldown = 0
        lastTimeHitEntity = 0L
        lastTimeSold = 0L
        playersFishHook = null
        placingTotem = false
        goldenFishEntity = null
        lookForGolenFish = false
        enemyEntity = null
    }
    //TODO: Fix Lag?
    @SubscribeEvent
    fun onPlayerTick(event: PlayerTickEvent) {
        if (mc.thePlayer == null) return
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
        if (killing) return
        if (pauseMacro) return
        if (goldenFishEntity != null && System.currentTimeMillis() - lastGoldenFishLookTime > 9) {
            lastGoldenFishLookTime = System.currentTimeMillis()
            if (System.currentTimeMillis() - lastTimeReeled > 5000){
                printdev("No activity with Golden Fish, Recasting")
                reelIn(true)
            }
            val yawAndPitch = VecToYawPitch(
                goldenFishEntity!!.positionVector.addVector(0.0, goldenFishEntity!!.eyeHeight.toDouble(),0.0),
                mc.thePlayer.positionVector,
            )
            printdev("Rotating to Golden Fish")
            PlayerRotation(
                PlayerRotation.Rotation(yawAndPitch.first, yawAndPitch.second),
                10L
            )
            return
        }
        if (fullInventory() && System.currentTimeMillis() - lastTimeSold > 10000 && Config.funnyFishingAutoSell){
            lastTimeSold = System.currentTimeMillis()
            mc.thePlayer.sendChatMessage("/bz")
            MinecraftForge.EVENT_BUS.register(InstaSell())
        }
        if (System.currentTimeMillis() - lastTimeReeled > Config.funnyFishingMaxTimeWithoutCatch * 1000){
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
                UChat.chat("$prefix Bobber was on the ground, recasting.")
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
            if (System.currentTimeMillis() - rotateCooldown >= 10000) {
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
            if (System.currentTimeMillis() - movementCooldown >= 15000 && mc.currentScreen == null) {
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
            Thread.sleep(1500)
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
                if (blockAtBlockPos != Blocks.air && blockAtBlockPos != Blocks.water && blockAtBlockPos != Blocks.flowing_water && blockAtBlockPos != Blocks.lava && blockAtBlockPos != Blocks.flowing_lava && blockOverBlockPos == Blocks.air)
                {
                    printdev("Found Proper Block: $blockPos Block Type: $blockAtBlockPos")
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

            val sneakKey = mc.gameSettings.keyBindSneak.keyCode
            KeyBinding.setKeyBindState(sneakKey, true)
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
                var xOffset = ((Math.random() * (1 - -1)) + -1)
                var zOffset = ((Math.random() * (1 - -1)) + -1)
                var targetBlock = mc.theWorld.getChunkFromBlockCoords(BlockPos(startingPosition)).getBlock(BlockPos(startingPosition!!.add(Vec3(xOffset,-1.0,zOffset))))

                while ((targetBlock == Blocks.air || targetBlock is BlockDynamicLiquid || targetBlock is BlockStaticLiquid) && Config.funnyFishing && System.currentTimeMillis() - moveStartedTime < 2000){
                    xOffset = ((Math.random() * (1 - -1)) + -1)
                    zOffset = ((Math.random() * (1 - -1)) + -1)
                    targetBlock = mc.theWorld.getChunkFromBlockCoords(BlockPos(startingPosition)).getBlock(BlockPos(startingPosition!!.add(Vec3(xOffset,-1.0,zOffset))))
                }
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

            KeyBinding.setKeyBindState(sneakKey, false)
            return@runAsync
        }
    }

    private fun getMobsWithinAABB(entity: Entity) {
        val aabb = AxisAlignedBB(entity.posX + 6, entity.posY - 3.0, entity.posZ + 6, entity.posX - 6, entity.posY + 5, entity.posZ - 6)
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
                if (enemyEntity != null) return
                if (!e.entityBoundingBox.intersectsWith(aabb)) continue@entity
                if (e !is EntityArmorStand) continue@entity
                if (!e.hasCustomName()) continue@entity
                if (e.customNameTag.stripColor().contains("0/")) continue@entity
                if (e.customNameTag.stripColor().contains("[Lv") && e.customNameTag.contains("❤")){
                    enemyEntity = e
                    printdev("Detected Enemy In Range: ${e.customNameTag}")
                    killing = true
                    Multithreading.runAsync{
                        if (funnyFishingAutoHook){
                            if (e.customNameTag.contains("Lava Flame") && !seenFlames.contains(e.entityId)){
                                seenFlames.add(e.entityId)
                                printdev("Its Lava Flame")
                                val grappleshotSlotIndex = findItemInHotbar("Moody Grappleshot")
                                if (grappleshotSlotIndex == -1){
                                    addMessage("$prefix Haven't found Grappleshot for Lava Flame, skipping")
                                } else {
                                    printdev("Attempting to hook it")
                                    mc.thePlayer.inventory.currentItem = grappleshotSlotIndex
                                    val yawAndPitch = VecToYawPitch(e.positionVector,mc.thePlayer.positionVector)
                                    PlayerRotation(PlayerRotation.Rotation(yawAndPitch.first, yawAndPitch.second), 300L)
                                    Thread.sleep(350)
                                    mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.heldItem)
                                    Thread.sleep(100)
                                }
                            }
                        }

                    val witherBlades = listOf("Scylla", "Astraea", "Hyperion", "Valkyrie")
                    var bladeSlotIndex = -1
                    for (blade in witherBlades) {
                        if (findItemInHotbar(blade) != -1) {
                            bladeSlotIndex = findItemInHotbar(blade)
                            break
                        }
                    }
                    if (bladeSlotIndex == -1) {
                        addMessage("$prefix Haven't Found Wither Blade!")
                        killing = false
                        return@runAsync
                    }
                        var lastTimeAttacked = 0L
                        Thread.sleep(Config.funnyFishingAutoKillingDelay.toLong())
                        printdev("Changing slot to Blade")
                        mc.thePlayer.inventory.currentItem = bladeSlotIndex
                        Thread.sleep(100)
                        printdev("Looking Down")
                        PlayerRotation(PlayerRotation.Rotation(mc.thePlayer.rotationYaw, 90f), 300L)
                        Thread.sleep(400)
                        printdev("Loop Start")
                        while (killing && Config.funnyFishing && enemyEntity != null && enemyEntity!!.isEntityAlive){
                            if (System.currentTimeMillis() - lastTimeAttacked > 160){
                                printdev("Attacking")
                                mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.heldItem)
                                lastTimeAttacked = System.currentTimeMillis()
                            }
                        }
                        printdev("Loop End")
                        lastTimeHitEntity = System.currentTimeMillis()
                    }
                    return
                }
            }
        }
    }

    @SubscribeEvent
    fun onWorldRender(event: RenderWorldLastEvent) {
        if (!Config.funnyFishing) return
        if (Config.focusOnGoldenFish) {
            if (lookForGolenFish && goldenFishEntity == null) {
                printdev("Looking for Golden Fish Entity")
                for (entity in mc.theWorld.loadedEntityList){
                    if (entity !is EntityArmorStand) continue
                    if (mc.thePlayer.getDistanceToEntity(entity) > 7) continue
                    if (entity.getCurrentArmor(3) != null && entity.getCurrentArmor(0) == null && entity.getCurrentArmor(1) == null && entity.getCurrentArmor(2) == null) {
                        goldenFishEntity = entity
                        printdev("Golden Fish Entity Found")
                    }
                }
            }
        }
        if (enemyEntity != null) if (!enemyEntity!!.isEntityAlive) enemyEntity = null
        if (enemyEntity != null) if (mc.thePlayer.getDistanceToEntity(enemyEntity!!) > 6) enemyEntity = null
        if (Config.fishingKilling == 2 && enemyEntity == null){
            getMobsWithinAABB(mc.thePlayer)
            if (lastTimeHitEntity != 0L && System.currentTimeMillis() - lastTimeHitEntity > 2000){
                lastTimeHitEntity = 0L
                Multithreading.runAsync {
                    Thread.sleep(400)
                    printdev("Looking At Old Location")
                    val yawAndPitch = blockPosToYawPitch(
                        BlockPos(mainLookAtBlock!!.blockPos.x + 1, mainLookAtBlock!!.blockPos.y, mainLookAtBlock!!.blockPos.z),
                        mc.thePlayer.positionVector
                    )
                    PlayerRotation(
                        PlayerRotation.Rotation(yawAndPitch.first, yawAndPitch.second),
                        600L
                    )
                    Thread.sleep(100)
                    printdev("Changing slot to Rod")
                    mc.thePlayer.inventory.currentItem = getFishingRod()
                    Thread.sleep(500)
                    printdev("Casting")
                    mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.heldItem)
//                    rotateCooldown = System.currentTimeMillis()
//                    movementCooldown = System.currentTimeMillis()
                    killing = false
                    printdev("Disabling Killing Variable")
                }
            }
//            var lastAttacked = 0L
//            while (killing && Config.funnyFishing) {
//                if (System.currentTimeMillis() - lastAttacked > 1000){
//                    mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.heldItem)
//                    lastAttacked = System.currentTimeMillis()
//                }
//            }

        }
        RenderUtils.drawFishingBox(mainLookAtBlock!!.blockPos, Color(236, 204, 8, 255), event.partialTicks)
    }

    //You spot a Golden Fish surface from beneath the lava!
    //The Golden Fish escapes your hook but looks weakened.
    //TROPHY FISH! You caught a Golden Fish

    fun reelIn(recast: Boolean) {
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
        if (event.message.unformattedText.stripColor() == "You were spawned in Limbo." || event.message.unformattedText.stripColor() == "A kick occurred in your connection, so you were put in the SkyBlock lobby!"){
            Multithreading.runAsync {
                Thread.sleep(1000)
                mc.thePlayer.sendChatMessage("/lobby")
                    Thread.sleep(2000)
                do {
                    mc.thePlayer.sendChatMessage("/play sb")
                    Thread.sleep(10000)
                } while (!SBInfo.onSkyblock)
            }
        }
        if (!Config.funnyFishing) return
        if (event.message.unformattedText.stripColor().startsWith(" ☠ You were killed by")) {
            addMessage("$prefix You died, applying failsafe")
            Config.funnyFishing = false
            rotateCooldown = 0
            lastTimeHitEntity = 0L
            lastTimeSold = 0L
            playersFishHook = null
            placingTotem = false
            goldenFishEntity = null
            lookForGolenFish = false
            enemyEntity = null
        }
        if (event.message.unformattedText.stripColor() == "You spot a Golden Fish surface from beneath the lava!") {
            printdev("Start Looking For Golden Fish")
            lookForGolenFish = true
            reelIn(false)
            Multithreading.runAsync {
                Thread.sleep(1000)
                printdev("Attempting To Hook Golden Fish")
                reelIn(false)
            }
        }
        if (event.message.unformattedText.stripColor().startsWith("The Golden Fish is weak!")) reelIn(false)
        if (event.message.unformattedText.stripColor().startsWith("TROPHY FISH! You caught a Golden Fish")) {
            goldenFishEntity = null
            lookForGolenFish = false
            Multithreading.runAsync {
                Thread.sleep(2000)
                printdev("Recasting into lava")
                reelIn(false)
            }
        }
        if (event.message.unformattedText.stripColor() == "The Golden Fish escapes your hook but looks weakened." && Config.focusOnGoldenFish) {
            printdev("Recasting on Golden Fish")
            reelIn(false)
        }
        if (Config.fishingKilling == 0) return
        for (mobMessage in FishingTracker.seaCreatureMessages){
            if (event.message.unformattedText.stripColor().lowercase() == mobMessage.key.lowercase()){

                when (Config.fishingKilling) {
                    1 -> {
                        when (getFireVeil()){
                            -1 -> addMessage("$prefix Haven't Found Fire Veil Wand!")
                            else -> {
                                killing = true
                                Multithreading.runAsync{
                                    Thread.sleep(Config.funnyFishingAutoKillingDelay.toLong())
                                    mc.thePlayer.inventory.currentItem = getFireVeil()
                                    Thread.sleep(150)
                                    mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.heldItem)
                                    Thread.sleep(150)
                                    mc.thePlayer.inventory.currentItem = getFishingRod()
                                    Thread.sleep(150)
                                    mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.heldItem)
                                    killing = false
                                }
                            }
                        }
                    }
                    3 -> {
                        when (findItemInHotbar("Midas Staff")){
                            -1 -> addMessage("$prefix Haven't Found Midas Staff!")
                            else -> {
                                killing = true
                                Multithreading.runAsync{
                                    Thread.sleep(Config.funnyFishingAutoKillingDelay.toLong())
                                    mc.thePlayer.inventory.currentItem = findItemInHotbar("Midas Staff")
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
    }
}