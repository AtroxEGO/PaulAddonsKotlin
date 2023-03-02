package me.atroxego.pauladdons.features.autoExperiments

import PaulAddons.Companion.mc
import gg.essential.api.utils.Multithreading
import me.atroxego.pauladdons.config.Config
import me.atroxego.pauladdons.features.betterlootshare.ESP.logger
import me.atroxego.pauladdons.utils.Utils
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.inventory.GuiChest
import net.minecraft.init.Blocks
import net.minecraft.init.Items
import net.minecraft.inventory.ContainerChest
import net.minecraft.inventory.IInventory
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.network.play.client.C0EPacketClickWindow
import net.minecraftforge.client.event.GuiOpenEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent

object AutoSequencer {

    var currentSolver = SolverType.NONE

    enum class SolverType {
        NONE, CHRONOMATRON, ULTRASEQUENCER
    }

    // Ultrasequencer

    var stack: ItemStack? = null
    var containerIndex = 0

    //HOW TF DOES THAT WORK

    class UltrasequencerItem(stack: ItemStack, index: Int) {
        var stack: ItemStack? = stack
        var containerIndex = index
    }

    private val ultraSequencerOrder = HashMap<Int, UltrasequencerItem>()
    private var ultrasequencerReplayIndex = 0

    @SubscribeEvent
    fun onGuiOpen(event: GuiOpenEvent) {
        if (!Config.autoExperiments) return
        if (event.gui !is GuiChest) return
        currentSolver = SolverType.NONE;
//        if (!Utils.inSkyblock) return
        val openChestName = Utils.getGuiName(event.gui);
        if (openChestName != null) {
            if (!openChestName.contains("Stakes")) {
                if (openChestName.startsWith("Chronomatron")) currentSolver = SolverType.CHRONOMATRON
                else if (openChestName.startsWith("Ultrasequencer")) currentSolver = SolverType.ULTRASEQUENCER
            }
        }
    }

    fun processInventoryContents(fromTick: Boolean) {
        if (currentSolver != SolverType.CHRONOMATRON && !fromTick) return
        if (Minecraft.getMinecraft().currentScreen is GuiChest) {
            val chest: GuiChest = Minecraft.getMinecraft().currentScreen as GuiChest
            val container = chest.inventorySlots as ContainerChest
            val lower: IInventory = container.lowerChestInventory

            if (currentSolver == SolverType.ULTRASEQUENCER) {
                val timerStack = lower.getStackInSlot(lower.sizeInventory - 5) ?: return
                if (timerStack.item == Item.getItemFromBlock(Blocks.glowstone)) {
                    ultrasequencerReplayIndex = 0
                }
                if (timerStack.item == Items.clock) {
                    clickCorrect()
                }
                for (index in 0..lower.sizeInventory) {
                    val stack = lower.getStackInSlot(index)
                    if (stack != null && stack.item == Items.dye) {
                        if (ultraSequencerOrder.containsKey(stack.stackSize - 1)) {
                            val ultrasequencerItem: UltrasequencerItem = ultraSequencerOrder[stack.stackSize - 1]!!
                            ultrasequencerItem.containerIndex = index
                            ultrasequencerItem.stack = stack
                        } else {
                            ultraSequencerOrder[stack.stackSize - 1] = UltrasequencerItem(stack, index)
                        }
                    }
                }
                if (ultraSequencerOrder.size > 9 && Config.autoCloseExperiments){
                    mc.thePlayer.closeScreen()
                    return
                }
            } else {
                ultraSequencerOrder.clear()
            }
        }
    }

    var clicking = false

    fun clickCorrect() {
        if (clicking) return
        if (currentSolver == SolverType.NONE) return
        clicking = true
        Multithreading.runAsync {
            if (Minecraft.getMinecraft().currentScreen is GuiChest) {
                val chest: GuiChest = Minecraft.getMinecraft().currentScreen as GuiChest
                val container = chest.inventorySlots as ContainerChest
                val lower: IInventory = container.lowerChestInventory
                if (currentSolver == SolverType.ULTRASEQUENCER) {
                    val timerStack = lower.getStackInSlot(lower.sizeInventory - 5) ?: return@runAsync
                    if (timerStack.item != Items.clock) return@runAsync
                    for (turn in 0 until ultraSequencerOrder.size) {
                        logger.info("${ultraSequencerOrder.size} $turn")
                        if (ultraSequencerOrder[turn] != null) {
                            logger.info(ultraSequencerOrder[turn]!!.containerIndex)
                        } else logger.info("Null")
                        mc.netHandler.addToSendQueue(
                            C0EPacketClickWindow(
                                container.windowId,
                                ultraSequencerOrder[turn]!!.containerIndex,
                                0,
                                0,
                                null,
                                0
                            )
                        )
                        container.slotClick(ultraSequencerOrder[turn]!!.containerIndex, 0, 0, mc.thePlayer)
                        Thread.sleep(Config.autoExperimentsDelay.toLong())
                    }
                }
                Thread.sleep(800)
                clicking = false
            }
        }
    }

    @SubscribeEvent
    fun onTick(event: ClientTickEvent) {
        if (!Config.autoExperiments) return
        if (Minecraft.getMinecraft().currentScreen !is GuiChest) {
            currentSolver = SolverType.NONE
            clicking = false
        }
        if (event.phase != TickEvent.Phase.END) {
            return
        }
        processInventoryContents(true)
    }
}