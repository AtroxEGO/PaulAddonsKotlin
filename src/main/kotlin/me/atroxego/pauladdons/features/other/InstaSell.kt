package me.atroxego.pauladdons.features.other

import gg.essential.api.utils.Multithreading
import gg.essential.universal.UChat
import me.atroxego.pauladdons.features.Feature
import me.atroxego.pauladdons.utils.Utils
import me.atroxego.pauladdons.utils.Utils.stripColor
import net.minecraft.client.gui.inventory.GuiChest
import net.minecraft.network.play.client.C0EPacketClickWindow
import net.minecraftforge.client.event.GuiScreenEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.EventPriority
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

class InstaSell : Feature() {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    fun onDrawBazzarScreen(event: GuiScreenEvent.DrawScreenEvent.Pre) {
        if (event.gui !is GuiChest) return
        val chest = event.gui as GuiChest
        mc.netHandler.addToSendQueue(C0EPacketClickWindow(chest.inventorySlots.windowId, 47, 0,0,chest.inventorySlots.inventory[47],0))
        MinecraftForge.EVENT_BUS.unregister(this)
        Multithreading.runAsync {
            Thread.sleep(500)
            mc.netHandler.addToSendQueue(C0EPacketClickWindow(mc.thePlayer.openContainer.windowId, 11, 0,0,mc.thePlayer.openContainer.inventory[11],0))
            mc.thePlayer.closeScreen()
        }
    }
}