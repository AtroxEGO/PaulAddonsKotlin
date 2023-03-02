package me.atroxego.pauladdons.features.autoExperiments

import gg.essential.api.utils.Multithreading
import me.atroxego.pauladdons.config.Config
import me.atroxego.pauladdons.events.impl.PacketEvent
import me.atroxego.pauladdons.features.Feature
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.inventory.GuiChest
import net.minecraft.init.Blocks
import net.minecraft.init.Items
import net.minecraft.inventory.ContainerChest
import net.minecraft.inventory.IInventory
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.network.play.client.C0EPacketClickWindow
import net.minecraft.network.play.server.S2FPacketSetSlot
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent

object AutoChromanotron : Feature(){

    var timerStack : ItemStack? = null
    var lastItemStack : ItemStack? = null
    var lastSlot = -1
    private val chronomatronIndexOrder: ArrayList<Int> = ArrayList()
    var clicking = false

    @SubscribeEvent
    fun onTick(event: TickEvent.ClientTickEvent){
        if (!Config.autoExperiments) return
        if (AutoSequencer.currentSolver != AutoSequencer.SolverType.CHRONOMATRON) return
        if (Minecraft.getMinecraft().currentScreen !is GuiChest) {
            chronomatronIndexOrder.clear()
            lastSlot = -1
            lastItemStack = null
            clicking = false
            return
        }
        val chest: GuiChest = Minecraft.getMinecraft().currentScreen as GuiChest
        val container = chest.inventorySlots as ContainerChest
        val lower: IInventory = container.lowerChestInventory
        timerStack = lower.getStackInSlot(lower.sizeInventory - 5) ?: return
        try {
            if (Config.autoCloseExperiments && lower.getStackInSlot(4).stackSize >= 13) {
                mc.thePlayer.closeScreen()
                return
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (clicking || timerStack!!.item != Items.clock) return
        clicking = true
        clickCorrect()
    }

    fun clickCorrect(){
        if (!Config.autoExperiments) return
        if (AutoSequencer.currentSolver != AutoSequencer.SolverType.CHRONOMATRON) return
        val chest: GuiChest = Minecraft.getMinecraft().currentScreen as GuiChest
        val container = chest.inventorySlots as ContainerChest
        Multithreading.runAsync{
            for (index in chronomatronIndexOrder){
                printdev("Clicking $index")
                mc.netHandler.addToSendQueue(C0EPacketClickWindow(container.windowId,index,0,0,container.lowerChestInventory.getStackInSlot(index),0))
                printdev("Sleeping for: ${Config.autoExperimentsDelay.toLong()}")
                Thread.sleep(Config.autoExperimentsDelay.toLong())
            }
            chronomatronIndexOrder.clear()
            clicking = false
        }
    }

    @SubscribeEvent
    fun onPacket(event: PacketEvent.ReceiveEvent){
        if (event.packet !is S2FPacketSetSlot) return
        if (!Config.autoExperiments) return
        if (AutoSequencer.currentSolver != AutoSequencer.SolverType.CHRONOMATRON) return
        val itemStack = event.packet.func_149174_e() ?: return
        if (timerStack == null) return
        if (!itemStack.isItemEnchanted || itemStack.displayName == null) return
        if (timerStack!!.item != Item.getItemFromBlock(Blocks.glowstone)) return
        if (lastItemStack == null || (itemStack.displayName != lastItemStack!!.displayName || lastSlot == event.packet.func_149173_d())){
            printdev(itemStack.displayName)
            chronomatronIndexOrder.add(event.packet.func_149173_d())
            lastSlot = event.packet.func_149173_d()
            lastItemStack = itemStack
        }
    }
}
