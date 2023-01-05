package me.atroxego.pauladdons.features.betterlootshare

import gg.essential.universal.UScreen
import me.atroxego.pauladdons.config.Config
import me.atroxego.pauladdons.features.Feature
import me.atroxego.pauladdons.render.DisplayNotification.displayNotification
import me.atroxego.pauladdons.utils.Utils.stripColor
import net.minecraft.client.Minecraft
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

object MobNotification : Feature() {
    private val entitySeen = arrayListOf<Int>()
    @SubscribeEvent
    fun checkForMob(event: RenderWorldLastEvent){
        if (UScreen.currentScreen.toString().contains("gg.essential.vigilance.gui.SettingsGui")) return
//        logger.info(UScreen.currentScreen.toString().contains("gg.essential.vigilance.gui.SettingsGui"))
        if (entitySeen.size > 100) {
//            logger.info("More Than 100 Entities Seen")
            return
        }
        if (!Config.mobNotification) return
        val world = Minecraft.getMinecraft().theWorld
        val entityList = world.loadedEntityList
        for (entity in entityList){
            if (entitySeen.contains(entity.entityId)) {
//                logger.info("$entity Has Been Seen")
                continue
            }
            if (!entity.hasCustomName()){continue}
//            for (mobForNotification in mobsForNotification){
//                if (!entity.customNameTag.stripColor().contains(mobForNotification.key, true)) continue
//                entitySeen.add(entity.entityId)
//                notificationMob = mobForNotification
//                val notificationString = notificationMob.value.plus("§l").plus(notificationMob.key)
//                logger.info("Displaying Notification : $notificationString")
//                displayNotification(notificationString,3000,true)
//                break
//            }
            var customMobs = getMobsForESP()
//            logger.info(customMobs)
            if (customMobs.endsWith(",")) customMobs = customMobs.removeSuffix(",")
            if (customMobs.endsWith(", ")) customMobs = customMobs.removeSuffix(", ")
//            logger.info(customMobs)
            for (customMob in customMobs.split(", ")){
                if (customMob == "") continue
                if (!entity.customNameTag.stripColor().contains(" $customMob ", true)) continue
//                logger.info(customMob)
                entitySeen.add(entity.entityId)
                val notificationString = "§6§l".plus(customMob)
                displayNotification(notificationString,3000,true)
                return
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

    @SubscribeEvent
    fun onWorldChange(event: WorldEvent.Load) {
        entitySeen.clear()
    }
}