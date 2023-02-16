package me.atroxego.pauladdons.features.dungeons

import me.atroxego.pauladdons.config.Config
import me.atroxego.pauladdons.events.impl.RenderEntityModelEvent
import me.atroxego.pauladdons.features.Feature
import me.atroxego.pauladdons.mixin.IMixinRendererLivingEntity
import me.atroxego.pauladdons.render.RenderUtils
import me.atroxego.pauladdons.utils.Utils
import me.atroxego.pauladdons.utils.Utils.stripColor
import net.minecraft.client.entity.EntityOtherPlayerMP
import net.minecraft.client.model.ModelBase
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.item.EntityArmorStand
import net.minecraft.entity.monster.EntityEnderman
import net.minecraft.entity.monster.EntityMob
import net.minecraft.entity.monster.EntitySkeleton
import net.minecraft.entity.monster.EntityZombie
import net.minecraft.entity.passive.EntityBat
import net.minecraft.util.AxisAlignedBB
import net.minecraft.util.MathHelper
import net.minecraft.world.World
import net.minecraft.world.chunk.Chunk
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

object StarredMobESP : Feature() {
    private val starredMobs = hashMapOf<Entity, EntityLivingBase>()
    private val customMobs = hashMapOf<Entity, EntityLivingBase>()

    @SubscribeEvent
    fun onRenderEntityModel(event: RenderEntityModelEvent){
        if (!Config.starredMobESP) return
        if (!Utils.inDungeon) return
        var maxHP: Float
        when (event.entity) {
            is EntityArmorStand -> {
                if (!event.entity.hasCustomName()) return
                val name = event.entity.customNameTag.stripColor()
                if (Config.starredMobESP && name.startsWith("✯ ")) {
                    val mob = starredMobs[event.entity]
                    if (mob != null) {
                        if (mob.isDead()) {
                            starredMobs.remove(event.entity)
                            return
                        }
                        val model = (mc.renderManager.getEntityRenderObject<EntityLivingBase>(mob) as IMixinRendererLivingEntity).mainModel
                        drawEsp(
                            mob,
                            model,
                            Config.starredMobESPColor.rgb,
                            1.0f
                        )
                    } else getStarredMobsWithinAABB(event.entity)
                } else if (name == "Wither Key" || name == "Blood Key")
                    RenderUtils.drawBeaconBeam(event.entity, Config.starredMobESPColor.rgb,3)
            }

            is EntityOtherPlayerMP ->
                if (Config.starredMobESP && event.entity.name?.trim() == "Shadow Assassin")
                    drawEsp(
                        event.entity,
                        event.model,
                        Config.starredMobESPColor.rgb,
                        1.0f
                    )

            is EntityEnderman ->
                if (event.entity.isInvisible)
                    event.entity.isInvisible = false

            is EntityBat -> if (event.entity.maxHealth.also { hp -> maxHP = hp } == 100.0f || maxHP == 200.0f) {
                RenderUtils.drawChamsEsp(
                    event.entity,
                    event.model,
                    Config.starredMobESPColor.rgb,
                    1.0f
                )
            }
        }
    }


    @SubscribeEvent
    fun onWorldLoad(event: WorldEvent.Load){
        customMobs.clear()
    }

    private fun drawEsp(entity: EntityLivingBase, model: ModelBase, color: Int, partialTicks: Float) {
        when (Config.starredESPType) {
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

    private fun getStarredMobsWithinAABB(entity: Entity) {
        val aabb = AxisAlignedBB(entity.posX + 0.4, entity.posY - 2.0, entity.posZ + 0.4, entity.posX - 0.4, entity.posY + 0.2, entity.posZ - 0.4)
        val i = MathHelper.floor_double((aabb.minX - 1.0)) shr 4
        val j = MathHelper.floor_double((aabb.maxX + 1.0)) shr 4
        val k = MathHelper.floor_double((aabb.minZ - 1.0)) shr 4
        val l = MathHelper.floor_double((aabb.maxZ + 1.0)) shr 4
        for (i1 in i..j)
            for (j1 in k..l)
                this.getStarredMobsWithinAABBForEntity(mc.theWorld.getChunkFromChunkCoords(i1, j1), entity, aabb)
    }

    private fun getStarredMobsWithinAABBForEntity(chunk: Chunk, entityIn: Entity, aabb: AxisAlignedBB) {
        val entityLists = chunk.entityLists
        var i = MathHelper.floor_double(((aabb.minY - World.MAX_ENTITY_RADIUS) / 16.0))
        var j = MathHelper.floor_double(((aabb.maxY + World.MAX_ENTITY_RADIUS) / 16.0))
        i = MathHelper.clamp_int(i, 0, (entityLists.size - 1))
        j = MathHelper.clamp_int(j, 0, (entityLists.size - 1))
        for (k in i..j) {
            if (entityLists[k].isEmpty()) continue
            entity@ for (e in entityLists[k]) {
                if (!e.entityBoundingBox.intersectsWith(aabb)) continue@entity
                when (e) {
                    is EntityOtherPlayerMP -> {
                        if (e.health <= 0.0f || e.getName() == null) continue@entity
                        when (e.getName().trim()) {
                            "Lost Adventurer", "Diamond Guy" -> starredMobs[entityIn] = (e as EntityLivingBase)
                            else -> {
                                if (e.isInvisible() || e.getUniqueID().version() != 2) continue@entity
                                starredMobs[entityIn] = (e as EntityLivingBase)
                            }
                        }
                    }

                    is EntitySkeleton, is EntityZombie -> {
                        if ((e as EntityMob).health <= 0.0f || e.isInvisible) continue@entity
                        starredMobs[entityIn] = (e as EntityLivingBase)
                    }

                    is EntityEnderman -> {
                        if (e.health <= 0.0f) continue@entity
                        starredMobs[entityIn] = (e as EntityLivingBase)
                    }
                }
            }
        }
    }

    private fun EntityLivingBase.isDead() = this.isDead || this.maxHealth <= 0f
}