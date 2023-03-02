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

import PaulAddons
import PaulAddons.Companion.keyBindings
import PaulAddons.Companion.mc
import PaulAddons.Companion.prefix
import me.atroxego.pauladdons.config.Config
import me.atroxego.pauladdons.events.impl.PacketEvent
import me.atroxego.pauladdons.features.betterlootshare.ESP.logger
import me.atroxego.pauladdons.gui.GuiElement
import me.atroxego.pauladdons.render.font.FontUtils
import me.atroxego.pauladdons.utils.Utils
import me.atroxego.pauladdons.utils.Utils.addMessage
import me.atroxego.pauladdons.utils.Utils.stripColor
import me.atroxego.pauladdons.utils.core.FloatPair
import net.minecraft.client.gui.inventory.GuiInventory
import net.minecraft.item.ItemStack
import net.minecraft.network.play.client.C0EPacketClickWindow
import net.minecraft.network.play.server.S02PacketChat
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.EventPriority
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import kotlin.math.roundToInt


object BonzoMask {

    init {
        BonzoMaskTimerGuiElement()
    }

    var hasBonzoInInv = false
    var nextBonzoUse = 0.0
    var bonzoMaskItem : ItemStack? = null

    class BonzoMaskTimerGuiElement : GuiElement("Bonzo Mask Timer", FloatPair(10, 10)) {
        override fun render() {
            if (!Config.bonzoMaskTimer || !Utils.inDungeon) return
            for (i in 0 until mc.thePlayer.inventory.sizeInventory){
                val itemStack = mc.thePlayer.inventory.getStackInSlot(i) ?: continue
                if (itemStack.displayName.stripColor().contains("Bonzo's Mask")){
                    hasBonzoInInv = true
                    bonzoMaskItem = itemStack
                    break
                }
                hasBonzoInInv = false
                bonzoMaskItem = null
            }
            if (!hasBonzoInInv) return
            if (bonzoMaskItem == null) return
            val timeNow = (System.currentTimeMillis()/1000).toDouble()
            val text = if (nextBonzoUse - timeNow < 0) "§aReady" else FontUtils.getTimeBetween(timeNow, nextBonzoUse)
            val textX = FontUtils.smartFontPlacement(16f, text.stripColor(), this)
            FontUtils.smartTexturePlacement(0f, this, "pauladdons/bonzoBasic.png", "pauladdons/bonzoMirror.png")
            fr.drawString(text, textX, 5f, 0xFFFFFF, true)

        }

        override fun demoRender() {
            val textX = FontUtils.smartFontPlacement(16f, "READY", this)
            FontUtils.smartTexturePlacement(0f, this, "pauladdons/bonzoBasic.png", "pauladdons/bonzoMirror.png")
            fr.drawString("§aREADY", textX, 5f, 0xFFFFFF, true)
        }

        override val toggled: Boolean
            get() = Config.bonzoMaskTimer
        override val height: Int
            get() = fr.FONT_HEIGHT
        override val width: Int
            get() = 20 + fr.getStringWidth("READY")

        init {
            PaulAddons.guiManager.registerElement(this)
        }
    }

    var timeWorldJoined = -1L
    @SubscribeEvent
    fun onWorldChange(event: WorldEvent.Load?) {
        nextBonzoUse = 0.0
        timeWorldJoined = System.currentTimeMillis()/1000
        displayedMessage = false
    }
//Your ⚚ Bonzo's Mask saved your life!
    @SubscribeEvent
    fun onChat(event: ClientChatReceivedEvent){
        val message = event.message.unformattedText

        if (!Utils.inDungeon) return
        if (bonzoMaskItem == null) return
        if (!message.contains("Bonzo's Mask") || !message.contains("saved your life!")  || message.contains(":")) return
        val usedTime = (System.currentTimeMillis() / 1000).toDouble()
        var cooldownSeconds = 0
        for (line in Utils.getItemLore(bonzoMaskItem!!)){
            if (line == null) continue
            if (line.stripColor().startsWith("Cooldown: ")) cooldownSeconds = line.stripColor().replace(Regex("[^\\d]"),"").toInt()
        }
        logger.info("Got Bonzo Mask Cooldown: $cooldownSeconds")
        if (cooldownSeconds > 0) nextBonzoUse = usedTime + cooldownSeconds
        if (mainHelmetSlotIndex != -1){
            addMessage("$prefix Switched back to ${mc.thePlayer.inventoryContainer.inventory[mainHelmetSlotIndex].displayName}")
            mc.displayGuiScreen(GuiInventory(mc.thePlayer))
            sendInventoryClick(mainHelmetSlotIndex)
            sendInventoryClick(5)
            sendInventoryClick(mainHelmetSlotIndex)
            mc.thePlayer.closeScreen()
            mainHelmetSlotIndex = -1
            mc.thePlayer.playSound("random.orb", 1.0f,0.5f)
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW, receiveCanceled = true)
    fun onChatPacket(event: PacketEvent.ReceiveEvent) {
        if (!Utils.inDungeon) return
        if (!Config.autoBonzoMask) return
        if (nextBonzoUse - System.currentTimeMillis()/1000 < 0)
        if (event.packet is S02PacketChat) {
        if (System.currentTimeMillis()/1000 - timeWorldJoined < 5) return
            if(event.packet.chatComponent.unformattedText.contains("❤") && event.packet.chatComponent.unformattedText.contains("❈") && event.packet.chatComponent.unformattedText.contains("✎") && !event.packet.chatComponent.unformattedText.contains(":")){
            val unformatted = event.packet.chatComponent.unformattedText
                val health = unformatted.split("/")[0].replace(",","").stripColor().toDouble()
                val maxHealth = unformatted.split(" ")[0].split("/")[1].replace("❤","").replace(",","").stripColor().toDouble()
                val healthPercent = (health/maxHealth * 100).roundToInt()
//                addMessage("Health: $health Max Health: $maxHealth Percent: $healthPercent")
                val helmetEquipped = mc.thePlayer.inventoryContainer.inventory[5]
                if (healthPercent < Config.autoBonzoMaskHealth * 100 || keyBindings[2]!!.isPressed){
                    if (helmetEquipped == null){
                    swapToBonzo()
                    addMessage("$prefix Automatically Equipped ${mc.thePlayer.inventoryContainer.inventory[5].displayName}")
                    } else if (!helmetEquipped.displayName.contains("Bonzo's Mask")){
                        swapToBonzo()
                    }
                }
                if (healthPercent > 50 && mainHelmetSlotIndex != -1){
                    addMessage("$prefix Switched back to ${mc.thePlayer.inventoryContainer.inventory[mainHelmetSlotIndex].displayName}")
                    mc.displayGuiScreen(GuiInventory(mc.thePlayer))
                    sendInventoryClick(mainHelmetSlotIndex)
                    sendInventoryClick(5)
                    sendInventoryClick(mainHelmetSlotIndex)
                    mc.thePlayer.closeScreen()
                    mainHelmetSlotIndex = -1
                    mc.thePlayer.playSound("random.orb", 1.0f,0.5f)
                }
            }
        }
    }
    private var displayedMessage = false
    private var mainHelmetSlotIndex = -1

    fun swapToBonzo(){
        var bonzoSlotIndex = -1
        for (slot in 9..43){
            val itemStack = mc.thePlayer.openContainer.getSlot(slot).stack ?: continue
            if (!itemStack.displayName.contains("Bonzo's Mask")) continue
            bonzoSlotIndex = slot
            break
        }
        if (bonzoSlotIndex == -1){
            if (!displayedMessage) {
                addMessage("$prefix Havent found Bonzo's Mask")
                displayedMessage = true
            }
            return
        }
        mc.displayGuiScreen(GuiInventory(mc.thePlayer))
        sendInventoryClick(bonzoSlotIndex)
        sendInventoryClick(5)
        sendInventoryClick(bonzoSlotIndex)
        mc.thePlayer.closeScreen()
        mainHelmetSlotIndex = bonzoSlotIndex
        mc.thePlayer.playSound("random.orb", 1.0f,0.5f)
        addMessage("$prefix Automatically Equipped ${mc.thePlayer.inventoryContainer.inventory[5].displayName}")
    }

    fun sendInventoryClick(slot: Int){
//        addMessage(mc.thePlayer.inventoryContainer.inventory[slot].displayName)
        val itemStack = mc.thePlayer.inventoryContainer.inventory[slot]
        mc.netHandler.addToSendQueue(C0EPacketClickWindow(GuiInventory(mc.thePlayer).inventorySlots.windowId, slot,0,0,itemStack,0))
        mc.thePlayer.openContainer.slotClick(slot,0,0,mc.thePlayer)
    }

}