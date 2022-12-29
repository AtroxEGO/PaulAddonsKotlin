package me.atroxego.pauladdons.features.betterlootshare

import me.atroxego.pauladdons.config.Config
import me.atroxego.pauladdons.features.Feature
import me.atroxego.pauladdons.features.betterlootshare.ESP.logger
import me.atroxego.pauladdons.render.DisplayNotification.displayNotification
import me.atroxego.pauladdons.utils.Utils.getCustomMobsReal
import me.atroxego.pauladdons.utils.Utils.getMobsForNotification
import me.atroxego.pauladdons.utils.Utils.stripColor
import net.minecraft.client.Minecraft
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

object MobNotification : Feature() {
    private val entitySeen = arrayListOf<Int>()
    lateinit var notificationMob : MutableMap.MutableEntry<String,String>
    @SubscribeEvent
    fun checkForMob(event: RenderWorldLastEvent){
        if (entitySeen.size > 100) {
//            logger.info("More Than 100 Entities Seen")
            return
        }
        if (!Config.mobNotification) return
        val world = Minecraft.getMinecraft().theWorld
        val entityList = world.loadedEntityList
        val mobsForNotification = getMobsForNotification()
        for (entity in entityList){
            if (entitySeen.contains(entity.entityId)) {
//                logger.info("$entity Has Been Seen")
                continue
            }
            if (!entity.hasCustomName()){continue}
            for (mobForNotification in mobsForNotification){
                if (!entity.customNameTag.stripColor().contains(mobForNotification.key, true)) continue
                entitySeen.add(entity.entityId)
                notificationMob = mobForNotification
                val notificationString = notificationMob.value.plus("§l").plus(notificationMob.key)
//                logger.info("Displayin Notification : $notificationString")
                displayNotification(notificationString,3000,true)
                break
            }
            if (Config.customESPMobs == "") return
            var customMobs = getCustomMobsReal()
//            logger.info(customMobs)
            if (customMobs.endsWith(",")) customMobs = customMobs.removeSuffix(",")
            if (customMobs.endsWith(", ")) customMobs = customMobs.removeSuffix(", ")
//            logger.info(customMobs)
            for (customMob in customMobs.split(", ")){
                if (customMob == "") continue
                if (!entity.customNameTag.stripColor().contains(customMob, true)) continue
//                logger.info(customMob)
                entitySeen.add(entity.entityId)
                val notificationString = "§6§l".plus(customMob)
                displayNotification(notificationString,3000,true)
                return
            }
        }
    }

    @SubscribeEvent
    fun onWorldChange(event: WorldEvent.Load) {
        entitySeen.clear()
    }
}