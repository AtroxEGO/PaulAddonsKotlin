package me.atroxego.pauladdons.features.slayers

import gg.essential.api.utils.Multithreading
import gg.essential.universal.UChat
import me.atroxego.pauladdons.config.Config
import me.atroxego.pauladdons.features.Feature
import me.atroxego.pauladdons.utils.Utils
import me.atroxego.pauladdons.utils.Utils.stripColor
import net.minecraft.client.settings.KeyBinding
import net.minecraftforge.event.entity.player.PlayerInteractEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import org.lwjgl.input.Keyboard

object StopNecromancyMenu : Feature(){

    @SubscribeEvent
    fun onPlayerInteract(event: PlayerInteractEvent){
        if (Config.stopOpeningNecromancyGUI)
        if (mc.thePlayer == null) return
        if (Utils.getScoreboardLines().size < 5) return
        if (!Utils.getScoreboardLines()[Utils.getScoreboardLines().size - 3].stripColor().contains("Slay the boss!")) return
        if (event.action == PlayerInteractEvent.Action.RIGHT_CLICK_AIR || event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {
            val item = mc.thePlayer.heldItem
            if (item != null && mc.thePlayer.isSneaking) {
                if (item.displayName.stripColor().contains("Necromancer Sword") || item.displayName.stripColor().contains("Reaper Scythe") || item.displayName.stripColor().contains("Summoning Ring")){
                    event.isCanceled = true
                }
            }
        }

    }
}