package me.atroxego.pauladdons.features.betterlootshare

import me.atroxego.pauladdons.config.Config
import me.atroxego.pauladdons.features.Feature
import me.atroxego.pauladdons.mixin.IMixinRendererLivingEntity
import me.atroxego.pauladdons.render.RenderUtils
import me.atroxego.pauladdons.utils.Utils.getRenderPartialTicks
import me.atroxego.pauladdons.utils.Utils.stripColor
import net.minecraft.client.model.ModelBase
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.item.EntityArmorStand
import net.minecraft.entity.monster.EntityMob
import net.minecraft.entity.player.EntityPlayerMP
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
//        logger.info("Test")
        if (event.entity is EntityPlayerMP) return
        if (event.entity is EntityArmorStand) {
            if (!event.entity.hasCustomName()) return
            val mobsForESP = getMobsForESP()
            if (mobsForESP == "" || mobsForESP == " ") return
            val name = event.entity.customNameTag.stripColor()
            for (cname in mobsForESP.split(", ")) {
//                if (name.endsWith(cname, true)) break
                if (!name.contains(" $cname ", true)) continue
                val mob = customMobs[event.entity]
                if (mob != null) {
//                    logger.info("mob != null")
                    if (mob.isDead()) {
                        customMobs.remove(event.entity)
                        break
                    }
                    val model = (mc.renderManager.getEntityRenderObject<EntityLivingBase>(mob) as IMixinRendererLivingEntity).mainModel
                    drawEsp(
                        mob,
                        model,
                        Config.glowColor.rgb,
                        getRenderPartialTicks()
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
                getRenderPartialTicks()
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
                if (!e.entityBoundingBox.intersectsWith(aabb)) continue@entity
                if (e !is EntityMob || e.health <= 0.0f || e.isInvisible) continue@entity
                customMobs[entityIn] = e as EntityLivingBase
            }
        }
    }

    private fun getMobsForESP(): String {
        var mobs = ""
//        if (!mobs.endsWith(", ")) mobs += ", "
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
//        for (mob in mobs.split(", ")) logger.info(mob)
        return mobs
    }

    private fun EntityLivingBase.isDead() = this.isDead || this.maxHealth <= 0f
}