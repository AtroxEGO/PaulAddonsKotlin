package me.atroxego.pauladdons.features.other

import PaulAddons.Companion.prefix
import gg.essential.api.utils.Multithreading
import me.atroxego.pauladdons.events.impl.PacketEvent
import me.atroxego.pauladdons.events.impl.PlayerAttackEntityEvent
import me.atroxego.pauladdons.events.impl.RenderEntityModelEvent
import me.atroxego.pauladdons.features.Feature
import me.atroxego.pauladdons.features.other.Ping.ping
import me.atroxego.pauladdons.render.RenderUtils.drawBox
import me.atroxego.pauladdons.utils.PlayerRotation
import me.atroxego.pauladdons.utils.SBInfo
import me.atroxego.pauladdons.utils.Utils
import me.atroxego.pauladdons.utils.Utils.addMessage
import me.atroxego.pauladdons.utils.Utils.findItemInHotbar
import me.atroxego.pauladdons.utils.Utils.stripColor
import net.minecraft.block.Block
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.monster.EntitySkeleton
import net.minecraft.entity.monster.EntityZombie
import net.minecraft.init.Items
import net.minecraft.network.play.client.C07PacketPlayerDigging
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement
import net.minecraft.network.play.server.S23PacketBlockChange
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumFacing
import net.minecraft.util.Vec3
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.event.world.WorldEvent.Load
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import java.awt.Color
import kotlin.math.pow

object AutoDojo : Feature() {
    var skeletonEntity : EntityLivingBase? = null
    var lookCooldown = 0L
    var autoDojoToggle = false
    var dojoType : DojoType = DojoType.NONE
    enum class DojoType {
        NONE, CONTROL, FORCE, MASTERY, DISCIPLINE
    }

    fun dojoToggle(){
        autoDojoToggle = !autoDojoToggle
        addMessage("$prefix Auto Dojo: ${if (autoDojoToggle) "§aOn" else "§cOff"}")
    }

    @SubscribeEvent
    fun onEntityRender(event: RenderEntityModelEvent){
        if (!autoDojoToggle) return
        // Test Of Control
        if (dojoType == DojoType.CONTROL && skeletonEntity == null){
            if (event.entity is EntitySkeleton && event.entity.skeletonType == 1 && event.entity.getDistanceToEntity(mc.thePlayer) < 25) {
                skeletonEntity = event.entity
                printdev("Detected Skeleton")
            }
        }
        if (event.entity is EntitySkeleton && event.entity.skeletonType == 1 && event.entity.getDistanceToEntity(mc.thePlayer) < 25) {
                if (dojoType == DojoType.CONTROL && skeletonEntity != null){
                    var x = skeletonEntity!!.posX + (skeletonEntity!!.posX - skeletonEntity!!.lastTickPosX) * (ping / 20)
                    var y = skeletonEntity!!.posY + (skeletonEntity!!.posY - skeletonEntity!!.lastTickPosY)
                    var z = skeletonEntity!!.posZ + (skeletonEntity!!.posZ - skeletonEntity!!.lastTickPosZ) * (ping / 20)
                    drawBox(Vec3(x, y, z),Color.cyan,event.partialTicks)
                    if (System.currentTimeMillis() - lookCooldown < 40) return
                    lookCooldown = System.currentTimeMillis()
                    val yawAndPitch = Utils.VecToYawPitch(Vec3(x, y + 1.5, z), mc.thePlayer.positionVector)
                    PlayerRotation(PlayerRotation.Rotation(yawAndPitch.first, yawAndPitch.second), 150L)
            }
        }
    }

    @SubscribeEvent
    fun onWorldUnload(event: Load){
        skeletonEntity = null
        lookCooldown = 0L
        dojoType = DojoType.NONE
    }


    @SubscribeEvent
    fun detectDojo(event: ClientChatReceivedEvent){
//        if (!autoDojoToggle) return
        if (SBInfo.mode != "crimson_isle") return
        val message = event.message.unformattedText.stripColor()
        when (message.replace(" ", "")) {
            "TestofControlOBJECTIVES" -> {
                dojoType = DojoType.CONTROL
                printdev("Test Of Control")
                addMessage("$prefix Current Dojo: Control")
            }
            "TestofForceOBJECTIVES" -> {
                dojoType = DojoType.FORCE
                printdev("Test Of Force")
                addMessage("$prefix Current Dojo: Force")
            }
            "TestofMasteryOBJECTIVES" -> {
                dojoType = DojoType.MASTERY
                printdev("Test Of Mastery")
                addMessage("$prefix Current Dojo: Mastery")
                val bowSlot = findItemInHotbar("Bow")
                if (bowSlot != -1) mc.thePlayer.inventory.currentItem = bowSlot
                mc.netHandler.addToSendQueue(C08PacketPlayerBlockPlacement(mc.thePlayer.heldItem))
            }
            "TestofDisciplineOBJECTIVES" -> {
                dojoType = DojoType.DISCIPLINE
                printdev("Test Of Discipline")
                addMessage("$prefix Current Dojo: Discipline")
            }
        }
        if (message.replace(" ","").contains("YourRank:")){
                printdev("Clearing")
                dojoType = DojoType.NONE
                skeletonEntity = null
                masteryBlocks.clear()
                addMessage("$prefix Current Dojo: None")
        }
    }

    @SubscribeEvent
    fun onPlayerEntityAttack(event: PlayerAttackEntityEvent){
//        printdev("Attack")
        if (!autoDojoToggle) return
        if (event.targetEntity !is EntityZombie) return
        val target = event.targetEntity
        when (dojoType){
            // Test Of Discipline
            DojoType.DISCIPLINE -> {
                val targetHelmet = target.getEquipmentInSlot(4) ?: return
                when (targetHelmet.item){
                    Items.leather_helmet -> {
                        val slot = findItemInHotbar("Wooden Sword")
                        if (slot != -1) mc.thePlayer.inventory.currentItem = slot
                        else printdev("Havent Found Wooden Sword")
                    }
                    Items.iron_helmet -> {
                        val slot = findItemInHotbar("Iron Sword")
                        if (slot != -1) mc.thePlayer.inventory.currentItem = slot
                        else printdev("Havent Found Iron Sword")
                    }
                    Items.golden_helmet -> {
                        val slot = findItemInHotbar("Golden Sword")
                        if (slot != -1) mc.thePlayer.inventory.currentItem = slot
                        else printdev("Havent Found Golden Sword")
                    }
                    Items.diamond_helmet -> {
                        val slot = findItemInHotbar("Diamond Sword")
                        if (slot != -1) mc.thePlayer.inventory.currentItem = slot
                        else printdev("Havent Found Diamond Sword")
                    }
                    else -> {}
                }
            }
            // Test of Force
            DojoType.FORCE -> {
                // Not Here
            }
            else -> {}
        }
    }

    class MasteryBlock(var pos: BlockPos, var timeTurnsRed: Long)

    var masteryBlocks = java.util.Vector<MasteryBlock>()

    @SubscribeEvent
    fun onblockChangePacket(event: PacketEvent.ReceiveEvent){
        if (!autoDojoToggle) return
        if (dojoType != DojoType.MASTERY) return
        if (event.packet !is S23PacketBlockChange) return
        val packet = event.packet
        if (mc.thePlayer.getDistanceSqToCenter(packet.blockPosition) < 2.0.pow(10.0)) {
            val state = Block.getStateId(packet.blockState)
            if (state == 16419) masteryBlocks.add(MasteryBlock(packet.blockPosition, System.currentTimeMillis() + 3000))
        }

    }
    @SubscribeEvent
    fun onTick(event: TickEvent.PlayerTickEvent){
        if (dojoType != DojoType.MASTERY) return
        if (!autoDojoToggle) return
        if (masteryBlocks.isEmpty()) return
//                printdev("Found Closest")

            val closestMasteryBlock = masteryBlocks.first()
        if (mc.thePlayer.heldItem == null || mc.thePlayer.heldItem.item != Items.bow) {
            val bowSlot = findItemInHotbar("Bow")
            if (bowSlot != -1) mc.thePlayer.inventory.currentItem = bowSlot
        }
            if (closestMasteryBlock.timeTurnsRed - System.currentTimeMillis() < 330){
                val x = if (closestMasteryBlock.pos.x < 0) closestMasteryBlock.pos.x + 0.5 else closestMasteryBlock.pos.x - 0.5
                val y = closestMasteryBlock.pos.y + 0.7
                val z = if (closestMasteryBlock.pos.z < 0) closestMasteryBlock.pos.z + 0.5 else closestMasteryBlock.pos.z - 0.5
                val yawAndPitch = Utils.VecToYawPitch(Vec3(x,y,z),mc.thePlayer.positionVector)
                PlayerRotation(PlayerRotation.Rotation(yawAndPitch.first, yawAndPitch.second),5L)
                masteryBlocks.remove(closestMasteryBlock)
                Multithreading.runAsync{
                    Thread.sleep(120)
                    mc.netHandler.addToSendQueue(C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM,BlockPos(0,0,0), EnumFacing.DOWN))
                    Thread.sleep(30)
                    mc.netHandler.addToSendQueue(C08PacketPlayerBlockPlacement(mc.thePlayer.heldItem))
                }
            }
    }
}