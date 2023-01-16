package me.atroxego.pauladdons.features.armorSwapper

import PaulAddons.Companion.mc
import PaulAddons.Companion.prefix
import net.minecraft.client.gui.inventory.GuiInventory
import net.minecraft.network.play.client.C0EPacketClickWindow
import net.minecraft.util.ChatComponentText

object ArmorSwapper {

    fun armorSwapper() {
        mc.displayGuiScreen(GuiInventory(mc.thePlayer))
        swapArmor()
        mc.thePlayer.closeScreen()
        mc.thePlayer.playSound("random.orb", 1.0f,0.5f)
    }

    private fun swapArmor(){
        val armorSlotsUsed = mutableListOf<Int>()
        for (i in 9..12){
            val inventorySlot = mc.thePlayer.openContainer.getSlot(i).stack ?: continue
            var armorSlot = 0
//            mc.thePlayer.sendChatMessage(inventorySlot.item.isValidArmor(inventorySlot,0,mc.thePlayer).toString())
            for(a in 0..3){
                if (inventorySlot.item.isValidArmor(inventorySlot,a,mc.thePlayer)) {
                if (armorSlotsUsed.contains(a + 5)) {

                    break
                }
                    armorSlot = a + 5
                    armorSlotsUsed.add(armorSlot)
                    break
                }
            }
            if (armorSlot == 0) continue
            swapPiece(armorSlot,i)
            mc.thePlayer.addChatMessage(ChatComponentText("$prefix Equipped: ${inventorySlot.displayName}"))
        }
    }



    private fun sendClick(slotIndex: Int){
        val windowId = GuiInventory(mc.thePlayer).inventorySlots.windowId
        val itemStack = mc.thePlayer.openContainer.getSlot(slotIndex).stack
        mc.netHandler.addToSendQueue(C0EPacketClickWindow(windowId, slotIndex,0,0,itemStack,0))
    }
    fun swapPiece(armorSlot: Int, inventorySlot: Int){
        sendClick(inventorySlot)
        mc.thePlayer.openContainer.slotClick(inventorySlot,0,0,mc.thePlayer)
        sendClick(armorSlot)
        mc.thePlayer.openContainer.slotClick(armorSlot,0,0,mc.thePlayer)
        mc.netHandler.addToSendQueue(C0EPacketClickWindow(GuiInventory(mc.thePlayer).inventorySlots.windowId, inventorySlot,0,0,null,0))
        mc.thePlayer.openContainer.slotClick(inventorySlot,0,0,mc.thePlayer)
    }
}