package me.atroxego.pauladdons.features.dungeons

import PaulAddons.Companion.mc
import PaulAddons.Companion.prefix
import me.atroxego.pauladdons.config.Config
import net.minecraft.client.gui.inventory.GuiInventory
import net.minecraft.network.play.client.C0EPacketClickWindow
import net.minecraft.util.ChatComponentText


object HelmetSwapper {
    var helmetOneLocation = -1
    var helmetTwoLocation = -1

    fun helmetSwapper(helmetOption: Int){
        mc.displayGuiScreen(GuiInventory(mc.thePlayer))
        swapHelmet(helmetOption)
        mc.thePlayer.closeScreen()
        mc.thePlayer.playSound("random.orb",1.0f,0.5f)
    }

    private fun swapHelmet(helmetOption: Int) {
        val helmetToLookFor = if (helmetOption == 1) Config.helmetToSwapNameOne else Config.helmetToSwapNameTwo
        if (helmetToLookFor == "") {
            mc.thePlayer.addChatMessage(ChatComponentText("$prefix Helmet Name Cannot Be Empty"))
            return
        }
        if (helmetTwoLocation != -1 || helmetOneLocation != -1){
        for (i in 5..8){
            val itemStack = mc.thePlayer.openContainer.getSlot(i).stack ?: continue
            if (itemStack.displayName.contains(helmetToLookFor)) {
                val slotIndex = if (helmetOption == 1) helmetOneLocation else helmetTwoLocation
                if (slotIndex == -1) break
                val helmetName = mc.thePlayer.inventory.getStackInSlot(slotIndex).displayName
                sendClick(slotIndex)
                mc.thePlayer.openContainer.slotClick(slotIndex,0,0,mc.thePlayer)
                sendClick(i)
                mc.thePlayer.openContainer.slotClick(i,0,0,mc.thePlayer)
                mc.netHandler.addToSendQueue(
                    C0EPacketClickWindow(
                        GuiInventory(mc.thePlayer).inventorySlots.windowId,
                        slotIndex,
                        0,
                        0,
                        null,
                        0
                    )
                )
                mc.thePlayer.openContainer.slotClick(slotIndex,0,0,mc.thePlayer)
                mc.thePlayer.addChatMessage(ChatComponentText("$prefix Equipped $helmetName"))
                return
            }
        }
        }
        for (i in 9..44){
            val itemStack = mc.thePlayer.openContainer.getSlot(i).stack ?: continue
            if (itemStack.displayName.contains(helmetToLookFor)) {
                var armorSlot = 0
                for (a in 0..3) {
                    if (itemStack.item.isValidArmor(itemStack, a, mc.thePlayer)) {
                        armorSlot = a + 5
                        break
                    }
                }
                sendClick(i)
                mc.thePlayer.openContainer.slotClick(i,0,0,mc.thePlayer)
                sendClick(armorSlot)
                mc.thePlayer.openContainer.slotClick(armorSlot,0,0,mc.thePlayer)
                mc.netHandler.addToSendQueue(
                    C0EPacketClickWindow(
                        GuiInventory(mc.thePlayer).inventorySlots.windowId,
                        i,
                        0,
                        0,
                        null,
                        0
                    )
                )
                mc.thePlayer.openContainer.slotClick(i,0,0,mc.thePlayer)
                mc.thePlayer.addChatMessage(ChatComponentText("$prefix Equipped ${itemStack.displayName}"))
                if (helmetOption == 1){
                    helmetOneLocation = i
                } else helmetTwoLocation = i
                return
            }
        }
        mc.thePlayer.addChatMessage(ChatComponentText("$prefix Havent found $helmetToLookFor"))
    }



    private fun sendClick(slotIndex: Int){
        val windowId = GuiInventory(mc.thePlayer).inventorySlots.windowId
        val itemStack = mc.thePlayer.openContainer.getSlot(slotIndex).stack
        mc.netHandler.addToSendQueue(C0EPacketClickWindow(windowId, slotIndex,0,0,itemStack,0))
    }
}