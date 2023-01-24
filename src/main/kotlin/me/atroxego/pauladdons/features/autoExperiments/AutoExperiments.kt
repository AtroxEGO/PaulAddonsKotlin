package me.atroxego.pauladdons.features.autoExperiments

import PaulAddons.Companion.mc
import gg.essential.api.utils.Multithreading
import me.atroxego.pauladdons.config.Config
import me.atroxego.pauladdons.features.betterlootshare.ESP.logger
import me.atroxego.pauladdons.utils.Utils
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.inventory.GuiChest
import net.minecraft.client.gui.inventory.GuiInventory
import net.minecraft.init.Blocks
import net.minecraft.init.Items
import net.minecraft.inventory.ContainerChest
import net.minecraft.inventory.IInventory
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.network.play.client.C0EPacketClickWindow
import net.minecraft.util.ChatComponentText
import net.minecraftforge.client.event.GuiOpenEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent

object AutoExperiments {

    var currentSolver = SolverType.NONE

    enum class SolverType {
        NONE, CHRONOMATRON, ULTRASEQUENCER
    }

    // Chronomatron

    private var addToChronomatron = false
    private var chronomatronStartSeq = false
    private val chronomatronOrder: ArrayList<String> = ArrayList()
    private val chronomatronIndexOrder: ArrayList<Int> = ArrayList()
    private var chronomatronReplayIndex = 0
    private var lastChronomatronSize = 0
    private const val millisLastClick: Long = 0

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
        chronomatronOrder.clear()
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
        //add config
//        if (!Utils.inSkyblock) return
//        mc.thePlayer.addChatMessage(ChatComponentText("Checking if in Skyblock"))

        if (Minecraft.getMinecraft().currentScreen is GuiChest) {
            val chest: GuiChest = Minecraft.getMinecraft().currentScreen as GuiChest
            val container = chest.inventorySlots as ContainerChest
            val lower: IInventory = container.lowerChestInventory

            if (currentSolver == SolverType.CHRONOMATRON) {
                if (Config.autoCloseExperiments && lower.getStackInSlot(4).stackSize >= 13) {
                    mc.thePlayer.closeScreen()
                    return
                }
                val timerStack = lower.getStackInSlot(lower.sizeInventory - 5) ?: return
                val isClock = timerStack.item == Items.clock
                var stainedHardenedClayName: String? = null
                var stainedHardenedClayIndex: Int? = null
                if (isClock) clickCorrect()
                for (index in 0..lower.sizeInventory) {
                    val stack = lower.getStackInSlot(index)
                    if (stack != null && stack.item == Item.getItemFromBlock(Blocks.stained_hardened_clay)) {
                        if (stack.tagCompound != null && stack.tagCompound.hasKey("ench")) {
                            if (stainedHardenedClayName != null && !stack.displayName.equals(stainedHardenedClayName)) return
                            stainedHardenedClayName = stack.displayName
                            stainedHardenedClayIndex = index
                        }
                    }
                }

                if (timerStack.item == Item.getItemFromBlock(Blocks.glowstone) || (isClock && (!addToChronomatron || chronomatronOrder.size < lastChronomatronSize + 1))) {
                    if (chronomatronStartSeq) {
                        chronomatronStartSeq = false
                        addToChronomatron = false
                        lastChronomatronSize = chronomatronOrder.size
                        chronomatronOrder.clear()
                    }

                    if (stainedHardenedClayName != null) {
                        if (addToChronomatron) {
                            chronomatronOrder.add(stainedHardenedClayName)
                            }
                        addToChronomatron = false
                    } else {
                        addToChronomatron = true
                        chronomatronReplayIndex = 0
                    }
                } else if (isClock) {
                    chronomatronStartSeq = true

                }
            } else {
                chronomatronStartSeq = true
                addToChronomatron = true
            }

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
                if (ultraSequencerOrder.size > 9){
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
                if (currentSolver == SolverType.CHRONOMATRON) {
                    val timerStack = lower.getStackInSlot(lower.sizeInventory - 5)
                    if (timerStack == null || timerStack.item != Items.clock){
                        clicking = false
                        return@runAsync
                    }
                    Thread.sleep(250)
                    for (turn in 0 until chronomatronOrder.size){
                        val block = chronomatronOrder[turn]
                        for (index in 0..lower.sizeInventory){
                            val stack = lower.getStackInSlot(index)
                            if (stack.displayName == block){
                                mc.netHandler.addToSendQueue(C0EPacketClickWindow(container.windowId,index,0,0,null,0))
//                                container.slotClick(index, 0, 0, mc.thePlayer) // MM NOT SURE ABOUT THAT
                                Thread.sleep(Config.autoExperimentsDelay.toLong())
                                break
                            }
                        }
                    }
                }
                Thread.sleep(700)
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