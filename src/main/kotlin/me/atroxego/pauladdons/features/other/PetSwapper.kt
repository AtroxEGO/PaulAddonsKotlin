package me.atroxego.pauladdons.features.other

import PaulAddons.Companion.prefix
import me.atroxego.pauladdons.features.Feature
import me.atroxego.pauladdons.utils.Utils.addMessage
import me.atroxego.pauladdons.utils.Utils.stripColor
import net.minecraft.client.gui.inventory.GuiChest
import net.minecraft.network.play.client.C0EPacketClickWindow
import net.minecraftforge.client.event.GuiScreenEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.EventPriority
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

class PetSwapper(private val arg: String) : Feature() {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    fun onDrawScreen(event: GuiScreenEvent.DrawScreenEvent.Pre) {
        if (event.gui !is GuiChest) return
        val chest = event.gui as GuiChest
        if (arg.contains(Regex("[^\\d]"))) {
            // Select Pet From Name
            var foundPet = false
            for (slot in 0..53){
                val itemStack = chest.inventorySlots.inventory[slot]?: break
                if (!itemStack.displayName.stripColor().startsWith("[")) continue
                val petName = itemStack.displayName.split(" ")[2].stripColor()
                if (petName.lowercase() == arg.lowercase()){
                    mc.netHandler.addToSendQueue(C0EPacketClickWindow(chest.inventorySlots.windowId, slot, 0,0,chest.inventorySlots.inventory[slot],0))
                    foundPet = true
                    break
                }
                printdev(petName)
            }
            if (!foundPet) addMessage("$prefix Haven't found pet $arg")
            foundPet = false
        } else {
            // Select Pet From Index
            val slot = if (arg.toInt() % 8 == 0) arg.toInt() + 2 else arg.toInt()
            val itemStack = chest.inventorySlots.inventory[slot + 9]
            if (itemStack == null) addMessage("$prefix Pet at $arg doesn't exist")
            else {
                printdev(itemStack.displayName)
                mc.netHandler.addToSendQueue(C0EPacketClickWindow(chest.inventorySlots.windowId, slot + 9, 0,0,chest.inventorySlots.inventory[arg.toInt() + 9],0))
        }
        }

        MinecraftForge.EVENT_BUS.unregister(this)
    }
}