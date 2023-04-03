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


package me.atroxego.pauladdons.features.other

import PaulAddons.Companion.mc
import gg.essential.universal.UChat
import me.atroxego.pauladdons.config.Config
import me.atroxego.pauladdons.events.impl.PacketEvent
import me.atroxego.pauladdons.utils.Utils
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.inventory.GuiChest
import net.minecraft.init.Blocks
import net.minecraft.inventory.ContainerChest
import net.minecraft.inventory.IInventory
import net.minecraft.item.Item
import net.minecraft.item.ItemBlock
import net.minecraft.network.Packet
import net.minecraft.network.play.client.C0EPacketClickWindow
import net.minecraft.network.play.server.S2FPacketSetSlot
import net.minecraftforge.client.event.GuiOpenEvent
import net.minecraftforge.client.event.GuiScreenEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent

object AutoMelody {


    private var lastInv = 0
    class Note (var slot: Int){
        var clicked = false
        var delay = 0
    }
    var melodyOpen = false
    private val notes = listOf(Note(37), Note(38), Note(39), Note(40), Note(41), Note(42), Note(43))

    @SubscribeEvent
    fun onGuiOpen(event: GuiOpenEvent) {
        if (!Config.autoMelody) return
        if (event.gui !is GuiChest) return
        melodyOpen = false
        val openChestName = Utils.getGuiName(event.gui);
        if (openChestName != null) {
            if (openChestName.startsWith("Harp")) {
                melodyOpen = true
            }

        }
    }

    @SubscribeEvent
    fun onRender(event: GuiScreenEvent.BackgroundDrawnEvent) {
        if (!Config.autoMelody) return
        if (!melodyOpen) return
        if (Minecraft.getMinecraft().currentScreen !is GuiChest) {
            melodyOpen = false
            return
        }
        val container = mc.thePlayer.openContainer ?: return
        val newHash = container.inventorySlots.subList(0,36).joinToString("") { it?.stack?.displayName ?: "" }.hashCode()
        if (lastInv == newHash) return
        lastInv = newHash
        for (ii in 0..6) {
            val slot = container.inventorySlots[37 + ii]
            if ((slot.stack?.item as? ItemBlock)?.block === Blocks.quartz_block) {
                mc.playerController.windowClick(
                    container.windowId,
                    slot.slotNumber,
                    2,
                    3,
                    mc.thePlayer
                )
                break
            }
        }
//        for (note in notes) {
//            if (note.delay > 0) note.delay--
//            val chest: GuiChest = Minecraft.getMinecraft().currentScreen as GuiChest
//            val container = chest.inventorySlots as ContainerChest
//            val lower: IInventory = container.lowerChestInventory
//            val itemStack = lower.getStackInSlot(note.slot) ?: return
//
//            if (itemStack.item == Item.getItemFromBlock(Blocks.stained_hardened_clay)) {
//                note.clicked = false
//                note.delay = 0
//            }
//            if (itemStack.item == Item.getItemFromBlock(Blocks.quartz_block)) {
//                if (note.clicked || note.delay != 0) return
//                if (lower.getStackInSlot(note.slot - 9).item == Item.getItemFromBlock(Blocks.wool)) note.delay = Config.autoMelodyCooldown
//                else note.clicked = true
//                mc.netHandler.addToSendQueue(C0EPacketClickWindow(container.windowId, note.slot, 0, 0, null, 0))
//            }
//        }
    }
}