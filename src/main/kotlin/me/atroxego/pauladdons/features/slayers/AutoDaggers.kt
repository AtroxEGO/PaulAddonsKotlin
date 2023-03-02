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

import PaulAddons.Companion.prefix
import me.atroxego.pauladdons.events.impl.PacketEvent
import me.atroxego.pauladdons.features.Feature
import me.atroxego.pauladdons.utils.Utils
import me.atroxego.pauladdons.utils.Utils.addMessage
import me.atroxego.pauladdons.utils.Utils.stripColor
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.util.AxisAlignedBB
import net.minecraft.util.MathHelper
import net.minecraft.world.World
import net.minecraft.world.chunk.Chunk
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

object AutoDaggers : Feature() {
    private val customMobs = hashMapOf<Entity, EntityLivingBase>()


    @SubscribeEvent
    fun onSendPacket(event: PacketEvent.SendEvent){
//        if (event.packet !is C08PacketPlayerBlockPlacement) return
//        printdev(event.packet.placedBlockOffsetX.toString())
//        if (SBInfo.mode != "crimson_isle") return
//        if (!Config.autoBlazeDaggers) return
//        if (event.packet !is C02PacketUseEntity) return
//        if (event.packet.action !=  C02PacketUseEntity.Action.ATTACK) return
//        val entity = event.packet.getEntityFromWorld(mc.theWorld)
//        if (entity !is EntityBlaze && entity !is EntityPigZombie && entity !is EntitySkeleton) return
//        var mob = customMobs[entity]
//        if (mob == null) {
//            getMobsWithinAABB(entity)
//        }
//        mob = customMobs[entity]
//        if (mob != null){
//            when (val attunement = mob.customNameTag.stripColor().split(" ")[0]){
//                "ASHEN" -> {
//                    val daggerSlot = findDagger(1)
//                    handleDaggers(daggerSlot, attunement)
//                }
//                "AURIC" -> {
//                    val daggerSlot = findDagger(1)
//                    handleDaggers(daggerSlot, attunement)
//                }
//                "SPIRIT" -> {
//                    val daggerSlot = findDagger(2)
//                    handleDaggers(daggerSlot, attunement)
//                }
//                "CRYSTAL" -> {
//                    val daggerSlot = findDagger(2)
//                    handleDaggers(daggerSlot, attunement)
//                }
//                "IMMUNE" -> {}
//                else -> addMessage("$prefix Encountered problem with reading $attunement attunement")
//            }
//            printdev("Current Mob Attunement ${mob.customNameTag.stripColor().split(" ")[0]}")
//        }
    }

    fun handleDaggers(daggerSlot : Int, attunement : String){
        if (mc.thePlayer.inventory.currentItem != daggerSlot){
        printdev("Dagger Slot: $daggerSlot Held Slot: ${mc.thePlayer.inventory.currentItem}")
        if (daggerSlot == -1){
            addMessage("$prefix Haven't found dagger for $attunement attunement")
            return
        }
        mc.thePlayer.inventory.currentItem = daggerSlot
        }
        var currentAttunement : String = ""
        for (line in Utils.getItemLore(mc.thePlayer.inventory.mainInventory[daggerSlot]!!)){
            if (line == null) continue
            if (line.stripColor().startsWith("Attuned: ")) currentAttunement = line.stripColor().split(" ")[1]
        }
        printdev("Current Dagger Attunement: $currentAttunement")
        if (attunement.lowercase() == currentAttunement.lowercase()) return
//        Multithreading.runAsync{
//            Thread.sleep(100)
            mc.playerController.sendUseItem(mc.thePlayer,mc.theWorld,mc.thePlayer.heldItem)
            printdev("Switching Attunements")
//        }
    }

    fun findDagger(type: Int) : Int{
            for (i in 0..7) {
                val item = mc.thePlayer.inventory.mainInventory[i] ?: continue
                if (type == 1){
                if (item.displayName.contains("Firedust Dagger") || item.displayName.contains("Kindlebane Dagger") || item.displayName.contains("Pyrochaos Dagger")) {
                    return i
                }
                } else {
                    if (item.displayName.contains("Twilight Dagger") || item.displayName.contains("Mawdredge Dagger") || item.displayName.contains("Deathripper Dagger")) {
                        return i
                    }
                }
            }
            return -1
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

    fun getMobsWithinAABBForEntity(chunk: Chunk, entityIn: Entity, aabb: AxisAlignedBB) {
        val entityLists = chunk.entityLists
        var i = MathHelper.floor_double((aabb.minY - World.MAX_ENTITY_RADIUS) / 16.0)
        var j = MathHelper.floor_double((aabb.maxY + World.MAX_ENTITY_RADIUS) / 16.0)
        i = MathHelper.clamp_int(i, 0, entityLists.size - 1)
        j = MathHelper.clamp_int(j, 0, entityLists.size - 1)
        for (k in i..j) {
            if (entityLists[k].isEmpty()) continue
            entity@ for (e in entityLists[k]) {
//                addMessage(e.name)
                if (!e.entityBoundingBox.expand(3.5,3.5,3.5).intersectsWith(aabb)) continue@entity
//                addMessage(e.name)
                if (!e.hasCustomName()) continue@entity
                if (!e.customNameTag.stripColor().contains("ASHEN") && !e.customNameTag.stripColor().contains("AURIC") && !e.customNameTag.stripColor().contains("SPIRIT") && !e.customNameTag.stripColor().contains("CRYSTAL")) continue@entity
                customMobs[entityIn] = e as EntityLivingBase
            }
        }
    }
}