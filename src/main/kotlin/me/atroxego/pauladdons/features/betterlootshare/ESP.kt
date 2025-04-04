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


package me.atroxego.pauladdons.features.betterlootshare

import me.atroxego.pauladdons.config.Config
import me.atroxego.pauladdons.features.Feature
import me.atroxego.pauladdons.mixin.IMixinRendererLivingEntity
import me.atroxego.pauladdons.render.RenderUtils
import me.atroxego.pauladdons.utils.Utils.stripColor
import net.minecraft.client.model.ModelBase
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.item.EntityArmorStand
import net.minecraft.entity.monster.EntityMob
import net.minecraft.util.AxisAlignedBB
import net.minecraft.util.MathHelper
import net.minecraft.world.World
import net.minecraft.world.chunk.Chunk
import net.minecraftforge.client.event.RenderLivingEvent
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

object ESP : Feature() {
    val logger: Logger = LogManager.getLogger("PaulAddons")
    private val customMobs = hashMapOf<Entity, EntityLivingBase>()

    @SubscribeEvent
    fun onRenderMob(event: RenderLivingEvent.Pre<EntityLivingBase>) {
        if (!Config.glowOnMob) return
        if (event.entity is EntityArmorStand) {
            if (!event.entity.hasCustomName()) return
            val mobsForESP = getMobsForESP()
            if (mobsForESP == "" || mobsForESP == " ") return
            val name = event.entity.customNameTag.stripColor()
            for (cname in mobsForESP.split(", ")) {
                if (!name.contains(" $cname ", true)) continue
                val mob = customMobs[event.entity]
                if (mob != null) {
                    if (mob.isDead()) {
                        customMobs.remove(event.entity)
                        break
                    }
                    val model = (mc.renderManager.getEntityRenderObject<EntityLivingBase>(mob) as IMixinRendererLivingEntity).mainModel
                    drawEsp(
                        mob,
                        model,
                        Config.glowColor.rgb,
                        1.0f
                    )
                    return
                } else getMobsWithinAABB(event.entity)
                return
            }
        } else{
            val mobsForESP = getMobsForESP()
            if (mobsForESP == "" || mobsForESP == " ") return
            for (cname in mobsForESP.split(", ")) {
            if (event.entity.name?.contains(" $cname ", true) == false) continue
            drawEsp(
                event.entity,
                event.renderer.mainModel,
                Config.glowColor.rgb,
                1.0f
            )
            return
        }
        }
    }

    @SubscribeEvent
    fun onWorldLoad(event: WorldEvent.Load){
        customMobs.clear()
    }

    private fun drawEsp(entity: EntityLivingBase, model: ModelBase, color: Int, partialTicks: Float) {
        when (Config.espSelector) {
            0 -> {
                RenderUtils.drawChamsEsp(entity, model, color, partialTicks)
            }
            1 -> {
                RenderUtils.renderBoundingBox(entity, color)
            }
            2 -> {
                RenderUtils.drawOutlinedEsp(entity, model, color, partialTicks)
            }
        }
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
                if (!e.entityBoundingBox.intersectsWith(aabb)) continue@entity
                if (e !is EntityMob || e.health <= 0.0f || e.isInvisible) continue@entity
                customMobs[entityIn] = e as EntityLivingBase
            }
        }
    }

    private fun getMobsForESP(): String {
        var mobs = ""
        if (Config.nutterNotification) mobs += "Nutcracker, "
        if (Config.gwSharkNotification) mobs += "Great White Shark, "
        if (Config.thunderNotification) mobs += "Thunder, "
        if (Config.jawbusNotification) mobs += "Lord Jawbus, "
        if (Config.grimNotification) mobs += "Grim Reaper, "
        if (Config.yetiNotification) mobs += "Yeti, "
        if (Config.hydraNotification) mobs += "Hydra, "
        if (Config.customESPMobs != "") mobs += Config.customESPMobs
        if (mobs.endsWith(",")) mobs = mobs.removeSuffix(",")
        if (mobs.endsWith(", ")) mobs = mobs.removeSuffix(", ")
        return mobs
    }

    private fun EntityLivingBase.isDead() = this.isDead || this.maxHealth <= 0f
}