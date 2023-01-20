package me.atroxego.pauladdons.features.dungeons

import PaulAddons.Companion.mc
import me.atroxego.pauladdons.config.Config
import me.atroxego.pauladdons.utils.Utils
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.gui.inventory.GuiChest
import net.minecraft.inventory.ContainerChest
import net.minecraftforge.client.event.GuiScreenEvent.BackgroundDrawnEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent


object AutoChestCloser {

    @SubscribeEvent
    fun onGuiBackgroundRender(event: BackgroundDrawnEvent) {
        if (event.gui is GuiChest && Utils.inSkyblock) {
            if (Utils.inDungeon && Config.autoCloseChest && getGuiName(event.gui).equals("Chest")) {
                mc.thePlayer.closeScreen()

//                mc.netHandler.networkManager.sendPacket(C0DPacketCloseWindow(mc.thePlayer.openContainer.windowId))
//                mc.netHandler.networkManager.sendPacket(C0DPacketCloseWindow((event.gui as GuiChest).inventorySlots.windowId))
            }
        }
    }

    private fun getGuiName(gui: GuiScreen?): String? {
        return if (gui is GuiChest) {
            (gui.inventorySlots as ContainerChest).lowerChestInventory.displayName.unformattedText
        } else ""
    }
}