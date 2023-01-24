package me.atroxego.pauladdons.features.dungeons

import me.atroxego.pauladdons.config.Config
import me.atroxego.pauladdons.features.Feature
import me.atroxego.pauladdons.mixin.IMixinRendererLivingEntity
import me.atroxego.pauladdons.render.RenderUtils
import me.atroxego.pauladdons.utils.Utils
import me.atroxego.pauladdons.utils.Utils.getRenderPartialTicks
import me.atroxego.pauladdons.utils.Utils.stripColor
import net.minecraft.client.Minecraft
import net.minecraft.client.model.ModelBase
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.item.EntityArmorStand
import net.minecraft.entity.monster.EntityMob
import net.minecraft.util.AxisAlignedBB
import net.minecraft.util.MathHelper
import net.minecraft.world.World
import net.minecraft.world.chunk.Chunk
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

object StarredMobESP : Feature() {
    private val customMobs = hashMapOf<Entity, EntityLivingBase>()

//    @SubscribeEvent
//    fun onRenderMob(event: RenderLivingEvent.Pre<EntityLivingBase>) {
//        if (!Config.starredMobESP) return
//        if (!Utils.inDungeon) return
////        if (event.entity is EntityPlayerMP) return
//        if (event.entity is EntityArmorStand) {
//            if (!event.entity.hasCustomName()) return
//            val name = event.entity.customNameTag.stripColor()
//                if (!name.startsWith("✯") && !name.contains("Mimic")) return
////                        && !name.contains("Lost Adventurer") && !name.contains("Angry Archaeologist") && !name.contains("Shadow Assassin") && !name.contains("Frozen Adventurer")
//                val mob = customMobs[event.entity]
//                if (mob != null) {
//                    if (mob.isDead()) {
//                        customMobs.remove(event.entity)
//                        return
//                    }
//                    val model = (mc.renderManager.getEntityRenderObject<EntityLivingBase>(mob) as IMixinRendererLivingEntity).mainModel
//                    drawEsp(
//                        mob,
//                        model,
//                        Config.starredMobESPColor.rgb,
//                        getRenderPartialTicks()
//                    )
//                    return
//                } else getMobsWithinAABB(event.entity)
//        } else{
//                if (event.entity.name?.startsWith("✯") == false) return
//                drawEsp(
//                    event.entity,
//                    event.renderer.mainModel,
//                    Config.starredMobESPColor.rgb,
//                    getRenderPartialTicks()
//                )
//                return
//            }
//        }

    @SubscribeEvent
    fun onRenderMob(event: RenderWorldLastEvent) {
        if (!Config.starredMobESP) return
        if (!Utils.inDungeon) return
//        if (event.entity is EntityPlayerMP) return
        val world = Minecraft.getMinecraft().theWorld
        val entityList = world.loadedEntityList
        for (entity in entityList) {
            if (entity.customNameTag.stripColor() == "Dinnerbone") entity.isInvisible = false
            if (entity is EntityArmorStand) {
                val name = entity.customNameTag.stripColor()
                if (!name.contains("✯") && !name.contains("Mimic") && !name.contains("Key")) continue
//                        && !name.contains("Lost Adventurer") && !name.contains("Angry Archaeologist") && !name.contains("Shadow Assassin") && !name.contains("Frozen Adventurer")
                val mob = customMobs[entity]
                if (mob != null) {
                    if (mob.isDead()) {
                        customMobs.remove(entity)
                        continue
                    }
                    val model = (mc.renderManager.getEntityRenderObject<EntityLivingBase>(mob) as IMixinRendererLivingEntity).mainModel
                    drawEsp(
                        mob,
                        model,
                        Config.starredMobESPColor.rgb,
                        getRenderPartialTicks()
                    )
                    continue
                } else getMobsWithinAABB(entity)
            } else {
                val name = entity.customNameTag.stripColor()
                if (!name.contains("✯") && !name.contains("Mimic") && !name.contains("Key")) continue
                drawEsp(
                    entity as EntityLivingBase,
                    (mc.renderManager.getEntityRenderObject<EntityLivingBase>(entity) as IMixinRendererLivingEntity).mainModel,
                    Config.starredMobESPColor.rgb,
                    getRenderPartialTicks()
                )
                continue
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

    private fun getMobsWithinAABB(entity: Entity) {
        val aabb = AxisAlignedBB(entity.posX + 0.4, entity.posY - 2.0, entity.posZ + 0.4, entity.posX - 0.4, entity.posY + 0.2, entity.posZ - 0.4)
        val i = MathHelper.floor_double(aabb.minX - 1.0) shr 4
        val j = MathHelper.floor_double(aabb.maxX + 1.0) shr 4
        val k = MathHelper.floor_double(aabb.minZ - 1.0) shr 4
        val l = MathHelper.floor_double(aabb.maxZ + 1.0) shr 4
        for (i1 in i..j)
            for (j1 in k..l)
                getMobsWithinAABBForEntity(mc.theWorld.getChunkFromChunkCoords(i1, j1), entity, aabb)
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
                if (!e.entityBoundingBox.intersectsWith(aabb)) continue@entity
                if (e !is EntityMob || e.health <= 0.0f || e.isInvisible) continue@entity
                customMobs[entityIn] = e as EntityLivingBase
            }
        }
    }

    private fun EntityLivingBase.isDead() = this.isDead || this.maxHealth <= 0f
}