package me.atroxego.pauladdons.features.slayers

import me.atroxego.pauladdons.PaulAddons.Companion.prefix
import gg.essential.api.utils.Multithreading
import gg.essential.universal.UChat
import me.atroxego.pauladdons.config.Config
import me.atroxego.pauladdons.features.Feature
import me.atroxego.pauladdons.utils.Utils
import me.atroxego.pauladdons.utils.Utils.findItemInHotbar
import me.atroxego.pauladdons.utils.Utils.findItemInInventory
import me.atroxego.pauladdons.utils.Utils.stripColor
import me.atroxego.pauladdons.utils.Utils.switchToItemInInventory
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

object AutoOrb : Feature(){

    val Deployables = listOf("Radiant", "Mana Flux", "Overflux", "Plasmaflux")
    var shouldNotify = true
    var orbSlotIndex = -1
    var nextTry = 0L
    var mainWeaponSlot = -1

    @SubscribeEvent
    fun onTick(event: RenderWorldLastEvent) {
        if (mc.thePlayer == null) return
        if (!Config.autoOrb) return
            if (mc.thePlayer == null || Utils.getScoreboardLines().size < 5) return
            if (!Utils.getScoreboardLines()[Utils.getScoreboardLines().size - 3].stripColor().contains("Slay the boss!")) {
                shouldNotify = true
                return
            }
            for (entity in mc.theWorld.loadedEntityList){
                if (!entity.hasCustomName()) continue
                for (deployableName in Deployables){
                    if (entity.customNameTag.stripColor().contains(deployableName, true) && mc.thePlayer.getDistanceToEntity(entity) < 18) return
                }
            }
        orbSlotIndex = if (findItemInInventory("Power Orb") == -1){
            if (findItemInHotbar("Power Orb") == -1) {
                if (shouldNotify && mc.thePlayer.inventory.itemStack == null && mc.currentScreen == null && System.currentTimeMillis() - nextTry < -3000) {
                    UChat.chat("$prefix Haven't Found a Power Orb")
                    shouldNotify = false
                }
                return
            } else findItemInHotbar("Power Orb")
        } else findItemInInventory("Power Orb")

        if (orbSlotIndex in 0..8){
            if (System.currentTimeMillis() - nextTry < 0) return
            nextTry = System.currentTimeMillis() + 7000
            val currentSlot = mc.thePlayer.inventory.currentItem
            Multithreading.runAsync {
                mc.thePlayer.inventory.currentItem = orbSlotIndex
                Thread.sleep(500)
                mc.netHandler.addToSendQueue(C08PacketPlayerBlockPlacement(mc.thePlayer.position.add(0, -1,0), 1, mc.thePlayer.heldItem, 0.5F,1.0F,0.5F))
                Thread.sleep(1000)
                mc.thePlayer.inventory.currentItem = currentSlot
            }
            return
        }

        if (orbSlotIndex in 9..35){
            if (System.currentTimeMillis() - nextTry < 0) return
            nextTry = System.currentTimeMillis() + 7000
            Multithreading.runAsync {
                switchToItemInInventory(orbSlotIndex)
                mainWeaponSlot = orbSlotIndex
                Thread.sleep(500)
                mc.netHandler.addToSendQueue(C08PacketPlayerBlockPlacement(mc.thePlayer.position.add(0, -1,0), 1, mc.thePlayer.heldItem, 0.5F,1.0F,0.5F))
                Thread.sleep(800)
                switchToItemInInventory(mainWeaponSlot)
            }

        }
    }
}