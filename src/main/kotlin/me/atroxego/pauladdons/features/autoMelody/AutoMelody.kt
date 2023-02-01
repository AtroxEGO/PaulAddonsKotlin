package me.atroxego.pauladdons.features.autoMelody

import PaulAddons.Companion.mc
import me.atroxego.pauladdons.config.Config
import me.atroxego.pauladdons.utils.Utils
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.inventory.GuiChest
import net.minecraft.init.Blocks
import net.minecraft.inventory.ContainerChest
import net.minecraft.inventory.IInventory
import net.minecraft.item.Item
import net.minecraft.network.play.client.C0EPacketClickWindow
import net.minecraftforge.client.event.GuiOpenEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent

object AutoMelody {
    class Note (var slot: Int){
        var clicked = false
        var delay = 0
    }
    var melodyOpen = false
    private val notes = listOf(Note(37),Note(38), Note(39),Note(40),Note(41),Note(42),Note(43))

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
    fun onTick(event: PlayerTickEvent){
        if (!Config.autoMelody) return
        if (!melodyOpen) return
        if (Minecraft.getMinecraft().currentScreen !is GuiChest) {
            melodyOpen = false
            return
        }
        for (note in notes){
            if (note.delay > 0) note.delay--
            val chest: GuiChest = Minecraft.getMinecraft().currentScreen as GuiChest
            val container = chest.inventorySlots as ContainerChest
            val lower: IInventory = container.lowerChestInventory
            val itemStack = lower.getStackInSlot(note.slot) ?: return

            if (itemStack.item == Item.getItemFromBlock(Blocks.stained_hardened_clay)){
                note.clicked = false
                note.delay = 0
            }
            if (itemStack.item == Item.getItemFromBlock(Blocks.quartz_block)){
                if (note.clicked || note.delay != 0) return
                if (lower.getStackInSlot(note.slot - 9).item == Item.getItemFromBlock(Blocks.wool)) note.delay = 7
                else note.clicked = true
                mc.netHandler.addToSendQueue(C0EPacketClickWindow(container.windowId,note.slot,0,0,null,0))
            }
        }
    }
}