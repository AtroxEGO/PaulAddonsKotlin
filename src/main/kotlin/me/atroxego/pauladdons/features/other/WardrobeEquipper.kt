package me.atroxego.pauladdons.features.other

import me.atroxego.pauladdons.PaulAddons.Companion.prefix
import gg.essential.api.utils.Multithreading
import gg.essential.universal.UChat
import me.atroxego.pauladdons.features.Feature
import me.atroxego.pauladdons.utils.Utils.stripColor
import net.minecraft.client.gui.inventory.GuiChest
import net.minecraft.network.play.client.C0EPacketClickWindow
import net.minecraftforge.client.event.GuiScreenEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.EventPriority
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

class WardrobeEquipper(private val arg: String) : Feature() {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    fun onDrawScreen(event: GuiScreenEvent.DrawScreenEvent.Pre) {
        if (event.gui !is GuiChest) return
        val chest = event.gui as GuiChest
        var foundArmor = false
        Multithreading.runAsync {
            Thread.sleep(200)
        for (index in 0..8){
            val helmet = chest.inventorySlots.inventory[index] ?: continue
            if (helmet.displayName.stripColor().contains(arg, true)) {
                foundArmor = true
                if (chest.inventorySlots.inventory[index + 36].displayName.stripColor().contains("Equipped")) {
                    UChat.chat("$prefix $arg is already equipped")
                } else mc.netHandler.addToSendQueue(C0EPacketClickWindow(chest.inventorySlots.windowId, index + 36, 0,0,chest.inventorySlots.inventory[index + 36],0))
            }
        }
            if (!foundArmor) {
                mc.netHandler.addToSendQueue(C0EPacketClickWindow(chest.inventorySlots.windowId, 53, 0,0,chest.inventorySlots.inventory[53],0))
                Thread.sleep(500)
                for (index in 0..8){
                    val helmet = mc.thePlayer.openContainer.inventory[index] ?: continue
                    if (helmet.displayName.stripColor().contains(arg, true)) {
                        foundArmor = true
                        if (mc.thePlayer.openContainer.inventory[index + 36].displayName.stripColor().contains("Equipped")) {
                            UChat.chat("$prefix $arg is already equipped")
                        } else mc.netHandler.addToSendQueue(C0EPacketClickWindow(mc.thePlayer.openContainer.windowId, index + 36, 0,0,mc.thePlayer.openContainer.inventory[index + 36],0))
                    }
                }
            }
            if (!foundArmor) {
                UChat.chat("$prefix Haven't Found $arg in your wardrobe!")
            }
            mc.thePlayer.closeScreen()
        }
        MinecraftForge.EVENT_BUS.unregister(this)
    }
}