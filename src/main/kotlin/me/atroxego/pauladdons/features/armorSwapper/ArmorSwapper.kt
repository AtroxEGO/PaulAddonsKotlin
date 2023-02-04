package me.atroxego.pauladdons.features.armorSwapper

import PaulAddons.Companion.mc
import PaulAddons.Companion.prefix
import me.atroxego.pauladdons.utils.Utils
import me.atroxego.pauladdons.utils.Utils.stripColor
import net.minecraft.client.gui.inventory.GuiInventory
import net.minecraft.item.ItemStack
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
                if (armorSlotsUsed.contains(a + 5)) break
                    if (a == 0 && !isHypixelHelmet(inventorySlot)) break
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
        val itemStack = if (mc.thePlayer.inventoryContainer.inventory[inventorySlot] == null) null else mc.thePlayer.inventoryContainer.inventory[inventorySlot]
        mc.netHandler.addToSendQueue(C0EPacketClickWindow(GuiInventory(mc.thePlayer).inventorySlots.windowId, inventorySlot,0,0,itemStack,0))
        mc.thePlayer.openContainer.slotClick(inventorySlot,0,0,mc.thePlayer)
    }

    fun isHypixelHelmet(item: ItemStack): Boolean{
        for (line in Utils.getItemLore(item)){
            if (line == null) continue
            if (line.stripColor().contains("HELMET")) return true
        }

        return false
    }
}