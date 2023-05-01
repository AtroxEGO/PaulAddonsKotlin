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


package me.atroxego.pauladdons.features.slayers

import me.atroxego.pauladdons.PaulAddons.Companion.mc
import me.atroxego.pauladdons.PaulAddons.Companion.prefix
import gg.essential.api.utils.Multithreading
import gg.essential.universal.UScreen
import me.atroxego.pauladdons.config.Config
import me.atroxego.pauladdons.features.other.ArmorSwapper.armorSwapper
import me.atroxego.pauladdons.features.other.PetSwapper
import me.atroxego.pauladdons.utils.Utils.addMessage
import me.atroxego.pauladdons.utils.Utils.stripColor
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.inventory.GuiInventory
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.network.play.client.C0EPacketClickWindow
import net.minecraft.util.AxisAlignedBB
import net.minecraft.util.MathHelper
import net.minecraft.world.World
import net.minecraft.world.chunk.Chunk
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import java.util.*

object AutoDaed {
    private val customMobs = hashMapOf<Entity, EntityLivingBase>()
    private var bossActive = false
    private var maxBossHealth = -1.0
    var dead = false
    var switching = false
    var lastHealth = -1.0
    var switched = false
    @SubscribeEvent
    fun checkForBoss(event: RenderWorldLastEvent){
        if (!Config.autoDaed) return
        if (UScreen.currentScreen.toString().contains("gg.essential.vigilance.gui.SettingsGui")) return
        val world = Minecraft.getMinecraft().theWorld
        val entityList = world.loadedEntityList
        for (entity in entityList){
            if (!entity.hasCustomName()) continue
            val name = entity.customNameTag.stripColor()
            if (name.contains("ⓉⓎⓅⒽⓄⒺⓊⓈ") || name.contains("ⓆⓊⒶⓏⒾⒾ")) return
            if (name != "Spawned by: ${mc.thePlayer.name}") continue
            bossActive = true
//            ☠ ⓉⓎⓅⒽⓄⒺⓊⓈ 10M❤ ☠ ⓆⓊⒶⓏⒾⒾ 10M❤
            val mob = customMobs[entity]
            if (mob != null) {
                val healthNumber = if (mob.customNameTag.contains("Hit")) lastHealth
                else {
                    val health = mob.customNameTag.stripColor().split(" ").last().replace("❤", "").lowercase()
                    if (health == "0") 0.0 else when (health.drop(health.length - 1)) {
                        "k" -> health.dropLast(1).toDouble() * 1000
                        "m" -> health.dropLast(1).toDouble() * 1000000
                        else -> health.toDouble()
                    }
                }
                lastHealth = healthNumber
                if (lastHealth == -1.0) return
                if (healthNumber == 0.0){
                    if (dead) return
                    Multithreading.runAsync{
                        Thread.sleep(1000)
                    switchBack()
                    slotWithMainWeapon = -1
                    dead = true
                    return@runAsync
                    }
                } else dead = false
                if (maxBossHealth < 0) maxBossHealth = healthNumber
                if (Config.daedSwapHealthType == 0){
                    if (healthNumber <= maxBossHealth * Config.percentageHealthDaed){
                        val slotWithDead = lookForDead()
                        if (switching) return
                        if (!switched){
                            if (Config.autoDaedArmorSwap) armorSwapper()
                            if (Config.autoDaedPetNameOne != ""){
                                mc.thePlayer.sendChatMessage("/pets")
                                MinecraftForge.EVENT_BUS.register(PetSwapper(Config.autoDaedPetNameOne))
                            }
                            switched = true
                        }
                        if (slotWithDead == null){
                            if (System.currentTimeMillis()/1000 - lastTimeChecked < 5 && lastTimeChecked > 0) return
                            addMessage("$prefix Haven't Found Daed")
                            lastTimeChecked = System.currentTimeMillis()/1000
                        }
                        else if (slotWithDead == -1) return
                        else {
                            switching = true
                            switchToDead(slotWithDead)
                            switching = false
                        }
                    }
                } else {
                    val healthNumberToSwapAt =
                        when (Config.manualHealthDaed.drop(Config.manualHealthDaed.length - 1).lowercase(Locale.getDefault())) {
                            "k" -> Config.manualHealthDaed.dropLast(1).toDouble() * 1000
                            "m" -> Config.manualHealthDaed.dropLast(1).toDouble() * 1000000
                            else -> Config.manualHealthDaed.toDouble()
                        }
                    if (healthNumber <= healthNumberToSwapAt) {
                        val slotWithDead = lookForDead()
                        if (switching) return
                        if (!switched){
                            if (Config.autoDaedArmorSwap) armorSwapper()
                            if (Config.autoDaedPetNameOne != ""){
                                mc.thePlayer.sendChatMessage("/pets")
                                MinecraftForge.EVENT_BUS.register(PetSwapper(Config.autoDaedPetNameOne))
                            }
                            switched = true
                        }
                        if (slotWithDead == null){
                            if (System.currentTimeMillis()/1000 - lastTimeChecked < 5 && lastTimeChecked > 0) return
                            addMessage("$prefix Haven't Found Daed")
                            lastTimeChecked = System.currentTimeMillis()/1000
                        }
                        else if (slotWithDead == -1) return
                        else {
                            switching = true
                            switchToDead(slotWithDead)
                            switching = false
                        }
                    }
                }
            } else {
                getMobsWithinAABB(entity)
            }
            return
        }
        if (bossActive) return
        bossActive = false
        maxBossHealth = -1.0
    }

    @SubscribeEvent
    fun onChat(event: ClientChatReceivedEvent){
        val message = event.message.unformattedText
        if (message.contains("SLAYER QUEST COMPLETE!") || message.startsWith(" ☠ You")) bossActive = false
    }

    fun getMobsWithinAABB(entity: Entity) {
        val aabb = AxisAlignedBB(entity.posX + 0.4, entity.posY - 2.0, entity.posZ + 0.4, entity.posX - 0.4, entity.posY + 0.2, entity.posZ - 0.4)
        val i = MathHelper.floor_double(aabb.minX - 1.0) shr 4
        val j = MathHelper.floor_double(aabb.maxX + 1.0) shr 4
        val k = MathHelper.floor_double(aabb.minZ - 1.0) shr 4
        val l = MathHelper.floor_double(aabb.maxZ + 1.0) shr 4
        for (i1 in i..j)
            for (j1 in k..l)
                this.getMobsWithinAABBForEntity(mc.theWorld.getChunkFromChunkCoords(i1, j1), entity, aabb)
    }

    @SubscribeEvent
    fun onWorldLoad(event: WorldEvent.Load){
        lastTimeSwitched = -1
        customMobs.clear()
        bossActive = false
        switched = false
        lastTimeChecked = -1
        maxBossHealth = -1.0
    }


    var lastTimeSwitched : Long = -1
    var lastTimeChecked : Long = -1

    fun lookForDead() : Int?{
        if (switching) return -1
        for (i in 0..7 ){
            val itemStack = mc.thePlayer.inventory.mainInventory[i] ?: continue
            if (itemStack.displayName.stripColor().contains("Daedalus Axe")){
                return i
            }
        }
        for (i in 9..35){
            val itemStack = mc.thePlayer.inventory.mainInventory[i] ?: continue
            if (!itemStack.displayName.stripColor().contains("Daedalus Axe")) continue
            return i
        }
        return null
    }

    var slotWithMainWeapon : Int = -1

    fun switchToDead(slotIndex: Int){
        if (System.currentTimeMillis()/1000 - lastTimeSwitched < 2) return
        lastTimeSwitched = System.currentTimeMillis()/1000
        if (slotIndex in 0..7){
            if (slotWithMainWeapon < 0) slotWithMainWeapon = mc.thePlayer.inventory.currentItem
            mc.thePlayer.inventory.currentItem = slotIndex
        } else {
            mc.displayGuiScreen(GuiInventory(mc.thePlayer))
            val windowId = GuiInventory(mc.thePlayer).inventorySlots.windowId
            var itemStack = mc.thePlayer.inventory.mainInventory[slotIndex]
            mc.netHandler.addToSendQueue(C0EPacketClickWindow(windowId, slotIndex,0,0,itemStack,0))
            itemStack = mc.thePlayer.inventory.mainInventory[mc.thePlayer.inventory.currentItem]
            mc.netHandler.addToSendQueue(C0EPacketClickWindow(windowId, mc.thePlayer.inventory.currentItem + 36,0,0,itemStack,0))
            itemStack = mc.thePlayer.inventory.mainInventory[slotIndex]
            slotWithMainWeapon = slotIndex
            mc.netHandler.addToSendQueue(C0EPacketClickWindow(windowId, slotIndex,0,0,itemStack,0))
            mc.thePlayer.closeScreen()
        }
    }

    fun switchBack(){
        if (switched){
            if (Config.autoDaedArmorSwap) armorSwapper()
            if (Config.autoDaedPetNameTwo != ""){
                mc.thePlayer.sendChatMessage("/pets")
                MinecraftForge.EVENT_BUS.register(PetSwapper(Config.autoDaedPetNameTwo))
            }
            switched = false
        }
        if (slotWithMainWeapon == -1) return
        if (slotWithMainWeapon in 0..7){
            mc.thePlayer.inventory.currentItem = slotWithMainWeapon
        } else {
            mc.displayGuiScreen(GuiInventory(mc.thePlayer))
            val windowId = GuiInventory(mc.thePlayer).inventorySlots.windowId
            var itemStack = mc.thePlayer.inventory.mainInventory[slotWithMainWeapon]
            mc.netHandler.addToSendQueue(C0EPacketClickWindow(windowId, slotWithMainWeapon,0,0,itemStack,0))
            itemStack = mc.thePlayer.inventory.mainInventory[mc.thePlayer.inventory.currentItem]
            mc.netHandler.addToSendQueue(C0EPacketClickWindow(windowId, mc.thePlayer.inventory.currentItem + 36,0,0,itemStack,0))
            itemStack = mc.thePlayer.inventory.mainInventory[slotWithMainWeapon]
            mc.netHandler.addToSendQueue(C0EPacketClickWindow(windowId, slotWithMainWeapon,0,0,itemStack,0))
            slotWithMainWeapon = -1
            mc.thePlayer.closeScreen()
        }
    }

    fun getMobsWithinAABBForEntity(chunk: Chunk, entityIn: Entity, aabb: AxisAlignedBB) {
        val entityLists = chunk.entityLists
        var i = MathHelper.floor_double((aabb.minY - World.MAX_ENTITY_RADIUS) / 16.0)
        var j = MathHelper.floor_double((aabb.maxY + World.MAX_ENTITY_RADIUS) / 16.0)
        i = MathHelper.clamp_int(i, 0, entityLists.size - 1)
        j = MathHelper.clamp_int(j, 0, entityLists.size - 1)
        for (k in i..j) {
            if (entityLists[k].isEmpty()) continue
            entity@ for (e in entityLists[k]) {
                if (!e.entityBoundingBox.intersectsWith(aabb)) continue@entity
                if (!e.hasCustomName()) continue@entity
                if (!e.customNameTag.stripColor().contains("☠")) continue@entity
                customMobs[entityIn] = e as EntityLivingBase
            }
        }
    }

}