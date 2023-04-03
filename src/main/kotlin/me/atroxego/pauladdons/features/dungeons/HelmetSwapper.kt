/*
 * Paul Addons - Hypixel Skyblock QOL Mod
 * Copyright (C) 2023  AtroxEGO
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */


package me.atroxego.pauladdons.features.dungeons

import PaulAddons.Companion.mc
import PaulAddons.Companion.prefix
import gg.essential.universal.UChat
import me.atroxego.pauladdons.config.Config
import me.atroxego.pauladdons.utils.Utils.addMessage
import net.minecraft.client.gui.inventory.GuiInventory
import net.minecraft.network.play.client.C0EPacketClickWindow
import net.minecraft.util.ChatComponentText


object HelmetSwapper {
    var helmetOneLocation = -1
    var helmetTwoLocation = -1
    var lastTimeSwapped = 0L

    fun helmetSwapper(helmetOption: Int){
        if (System.currentTimeMillis() - lastTimeSwapped > 300){
            mc.displayGuiScreen(GuiInventory(mc.thePlayer))
            swapHelmet(helmetOption)
            mc.thePlayer.closeScreen()
            mc.thePlayer.playSound("random.orb",1.0f,0.5f)
            lastTimeSwapped = System.currentTimeMillis()
        } else {
            mc.thePlayer.addChatMessage(ChatComponentText("$prefix Slow down a bit!"))
        }
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
                val helmetName = mc.thePlayer.inventoryContainer.inventory[slotIndex]?.displayName
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
                mc.thePlayer.addChatMessage(ChatComponentText("$prefix Equippedd ${itemStack.displayName}"))
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