package me.atroxego.pauladdons.features.slayers

import me.atroxego.pauladdons.config.Config
import me.atroxego.pauladdons.events.impl.RenderEntityModelEvent
import me.atroxego.pauladdons.features.Feature
import me.atroxego.pauladdons.mixin.IMixinRendererLivingEntity
import me.atroxego.pauladdons.render.RenderUtils
import me.atroxego.pauladdons.utils.Utils
import me.atroxego.pauladdons.utils.Utils.stripColor
import net.minecraft.client.model.ModelBase
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.item.EntityArmorStand
import net.minecraft.entity.monster.EntityMob
import net.minecraft.entity.passive.EntityWolf
import net.minecraft.util.AxisAlignedBB
import net.minecraft.util.MathHelper
import net.minecraft.world.World
import net.minecraft.world.chunk.Chunk
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

object SlayerESP : Feature() {
    private val customMobs = hashMapOf<Entity, EntityLivingBase>()

    @SubscribeEvent
    fun onRenderEntityModel(event: RenderEntityModelEvent) {
        if (!Config.slayerESP) return
        if (Utils.inDungeon) return
        if (event.entity is EntityArmorStand) {
            if (!event.entity.hasCustomName()) return
            val name = event.entity.customNameTag.stripColor()
//            addMessage(name)
            for (bname in getBossesForEsp()) {
                if (!name.contains(bname, true)) continue
                val mob = customMobs[event.entity]
                if (mob != null) {
//                    addMessage(name)
                    if (mob.isDead()) {
                        customMobs.remove(event.entity)
                        break
                    }
                    val model =
                        (mc.renderManager.getEntityRenderObject<EntityLivingBase>(mob) as IMixinRendererLivingEntity).mainModel
                    drawEsp(
                        mob,
                        model,
                        Config.bossESPColor.rgb,
                        event.partialTicks
                    )
                    break
                } else getMobsWithinAABB(event.entity)
                break
            }
            for (mini in getMinisForEsp()) {
                if (mini.key in 4..5){
                    val contains = name.contains(mini.value.name)
//                    logger.info("Does $name contain ${mini.value.name}? $contains")
                }
                if (!name.contains(mini.value.name, true)) continue
                val mob = customMobs[event.entity]
                if (mob != null) {
//                addMessage(name)
                    if (mob.isDead()) {
                        customMobs.remove(event.entity)
                        break
                    }
                    val model =
                        (mc.renderManager.getEntityRenderObject<EntityLivingBase>(mob) as IMixinRendererLivingEntity).mainModel
                    drawEsp(
                        mob,
                        model,
                        if (mini.value.color == 1) Config.worseMiniColor.rgb else Config.betterMiniColor.rgb,
                        event.partialTicks
                    )
                    break
                } else getMobsWithinAABB(event.entity)
                break
            }
        }
        else{
            for (bname in getBossesForEsp()) {
            if (event.entity.name?.contains(bname, true) == false) continue
            drawEsp(
                event.entity,
                event.model,
                Config.bossESPColor.rgb,
                event.partialTicks
            )
            break
        }
            for (mini in getMinisForEsp()) {
                if (event.entity.name?.contains(mini.value.name, true) == false) continue
                drawEsp(
                    event.entity,
                    event.model,
                    if (mini.value.color == 1) Config.worseMiniColor.rgb else Config.betterMiniColor.rgb,
                    event.partialTicks
                )
                break
            }
        }
    }


    @SubscribeEvent
    fun onWorldLoad(event: WorldEvent.Load){
        customMobs.clear()
    }

    private fun drawEsp(entity: EntityLivingBase, model: ModelBase, color: Int, partialTicks: Float) {
        when (Config.slayerESPType) {
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
//                addMessage(e.name)
                if (e is EntityWolf){
                    if (e.health <= 0.0f || e.isInvisible) continue@entity
                    customMobs[entityIn] = e as EntityLivingBase
                } else if (e is EntityMob){
                    if (e.health <= 0.0f || e.isInvisible) continue@entity
                    customMobs[entityIn] = e as EntityLivingBase
                }
//                if (e !is EntityMob || e.health <= 0.0f || e.isInvisible) continue@entity
//                addMessage("${e.name} is entitymob? ${e !is EntityMob}")

            }
        }
    }

    fun getBossesForEsp(): ArrayList<String> {
        val namesForESP: ArrayList<String> = ArrayList()
        if (Config.bossESP){
            namesForESP.add("Revenant Horror")
            namesForESP.add("Tarantula Broodfather")
            namesForESP.add("Sven Packmaster")
            namesForESP.add("Voidgloom Seraph")
            namesForESP.add("Inferno Demonlord")
        }
        return namesForESP
    }

    class SlayerMini(name: String, color: Int) {
        var name: String = name
        var color : Int = color
    }

    fun getMinisForEsp(): HashMap<Int, SlayerMini> {
        val namesForESP = HashMap<Int, SlayerMini>()
        if (Config.miniESP){
            namesForESP[0] = SlayerMini("Atoned Champion", 1)
            namesForESP[1] = SlayerMini("Atoned Revenant", 2)
            namesForESP[2] = SlayerMini("Tarantula Beast", 1)
            namesForESP[3] = SlayerMini("Mutant Tarantula", 2)
            namesForESP[4] = SlayerMini("Sven Follower", 1)
            namesForESP[5] = SlayerMini("Sven Alpha", 2)
            namesForESP[6] = SlayerMini("Voidling Radical", 1)
            namesForESP[7] = SlayerMini("Voidcrazed Maniac", 2)
            namesForESP[8] = SlayerMini("Kindleheart Demon", 1)
            namesForESP[9] = SlayerMini("Burningsoul Demon", 2)
            namesForESP[10] = SlayerMini("Flare Demon", 1)
        }
        return namesForESP
    }


    private fun EntityLivingBase.isDead() = this.isDead || this.maxHealth <= 0f
}