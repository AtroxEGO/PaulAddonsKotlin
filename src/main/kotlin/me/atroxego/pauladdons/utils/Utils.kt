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


package me.atroxego.pauladdons.utils

import PaulAddons.Companion.mc
import gg.essential.universal.wrappers.message.UTextComponent
import me.atroxego.pauladdons.events.impl.MainReceivePacketEvent
import me.atroxego.pauladdons.events.impl.PacketEvent
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.gui.inventory.GuiChest
import net.minecraft.client.gui.inventory.GuiInventory
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EnumPlayerModelParts
import net.minecraft.inventory.ContainerChest
import net.minecraft.item.ItemStack
import net.minecraft.network.play.client.C0EPacketClickWindow
import net.minecraft.network.play.server.S02PacketChat
import net.minecraft.util.*
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.common.MinecraftForge
import java.io.File
import java.io.IOException
import java.util.*
import kotlin.math.atan2
import kotlin.math.sqrt


object Utils {
    private val STRIP_COLOR_PATTERN = Regex("(?i)§[\\dA-FK-OR]")

    var skyblock = false

    val inSkyblock: Boolean
//        get() = true
        get() = SBInfo.mode == "SKYBLOCK" || skyblock
    val inDungeon
        get() = SBInfo.mode == "dungeon"

    fun sendItemTags(){
        val tags = mc.thePlayer.heldItem.tagCompound
        mc.thePlayer.addChatMessage(ChatComponentText("$tags"))
    }

    public fun getGuiName(gui: GuiScreen?): String? {
        return if (gui is GuiChest) {
            (gui.inventorySlots as ContainerChest).lowerChestInventory.displayName.unformattedText
        } else ""
    }

    fun getItemLore(itemStack: ItemStack): List<String?> {
        val NBT_INTEGER = 3
        val NBT_STRING = 8
        val NBT_LIST = 9
        val NBT_COMPOUND = 10
        if (itemStack.hasTagCompound() && itemStack.tagCompound.hasKey("display", NBT_COMPOUND)) {
            val display = itemStack.tagCompound.getCompoundTag("display")
            if (display.hasKey("Lore", NBT_LIST)) {
                val lore = display.getTagList("Lore", NBT_STRING)
                val loreAsList: MutableList<String> = ArrayList()
                for (lineNumber in 0 until lore.tagCount()) {
                    loreAsList.add(lore.getStringTagAt(lineNumber))
                }
                return Collections.unmodifiableList(loreAsList)
            }
        }
        return Collections.emptyList()
    }
    fun scoreboardData(){
        if(getScoreboardLines()[3].contains("F7") || getScoreboardLines()[3].contains("M7")) addMessage("True")
//        for (line in getScoreboardLines()){
//            mc.thePlayer.addChatMessage(ChatComponentText(line))
//        }
    }

    fun fullInventory() : Boolean {
        val inventory = mc.thePlayer.inventory.mainInventory
        for (i in 0..35){
            if (inventory[i] == null) return false
        }
        return true
    }

    fun addMessage(message: String){
        mc.thePlayer.addChatMessage(ChatComponentText(message))
    }

    fun getScoreboardLines(): List<String> {
        val lines = ScoreboardUtil.fetchScoreboardLines().map { it.stripControlCodes() }
        return lines
    }

    fun String?.stripControlCodes(): String = UTextComponent.stripFormatting(this ?: "")

    fun checkThreadAndQueue(run: () -> Unit) {
        if (!mc.isCallingFromMinecraftThread) {
            mc.addScheduledTask(run)
        } else run()
    }

    fun cancelChatPacket(ReceivePacketEvent: PacketEvent.ReceiveEvent) {
        if (ReceivePacketEvent.packet !is S02PacketChat) return
        ReceivePacketEvent.isCanceled = true
        val packet = ReceivePacketEvent.packet
        checkThreadAndQueue {
            MinecraftForge.EVENT_BUS.post(MainReceivePacketEvent(mc.netHandler, ReceivePacketEvent.packet))
            MinecraftForge.EVENT_BUS.post(ClientChatReceivedEvent(packet.type, packet.chatComponent))
        }
    }

    fun blockPosToYawPitch(blockPos: BlockPos, playerPos: Vec3): Pair<Float, Float> {
        val diffX = blockPos.x - playerPos.xCoord - 0.5
        val diffY = blockPos.y - (playerPos.yCoord + mc.thePlayer.getEyeHeight()) + 0.5
        val diffZ = blockPos.z - playerPos.zCoord + 0.5
        var yaw = (Math.toDegrees(atan2(diffZ, diffX))).toFloat() - 90.0f
        val dist = sqrt(diffX * diffX + diffZ * diffZ)
        val pitch = (-(Math.toDegrees(atan2(diffY, dist)))).toFloat()
        return Pair(
            mc.thePlayer.rotationYaw + MathHelper.wrapAngleTo180_float(yaw - mc.thePlayer.rotationYaw),
            mc.thePlayer.rotationPitch + MathHelper.wrapAngleTo180_float(pitch - mc.thePlayer.rotationPitch) )
    }

    fun VecToYawPitch(vec: Vec3, playerPos: Vec3): Pair<Float, Float> {
        val diffX = vec.xCoord - playerPos.xCoord
        val diffY = vec.yCoord - (playerPos.yCoord + mc.thePlayer.getEyeHeight()) + 0.5
        val diffZ = vec.zCoord - playerPos.zCoord
        var yaw = (Math.toDegrees(atan2(diffZ, diffX))).toFloat() - 90.0f
        val dist = sqrt(diffX * diffX + diffZ * diffZ)
        val pitch = (-(Math.toDegrees(atan2(diffY, dist)))).toFloat()
        return Pair(
            mc.thePlayer.rotationYaw + MathHelper.wrapAngleTo180_float(yaw - mc.thePlayer.rotationYaw),
            mc.thePlayer.rotationPitch + MathHelper.wrapAngleTo180_float(pitch - mc.thePlayer.rotationPitch) )
    }

    fun findItemInHotbar(name: String) : Int{
        for (slot in 0..8){
            val itemStack = mc.thePlayer.inventory.mainInventory[slot] ?: continue
            if (itemStack.displayName.stripColor().contains(name)) return slot
        }
        return -1
    }

    fun findItemInInventory(name: String) : Int{
        for (slot in 9..35){
            val itemStack = mc.thePlayer.inventory.getStackInSlot(slot) ?: continue
            if (itemStack.displayName.stripColor().contains(name)) return slot
        }
        return -1
    }

    fun switchToItemInInventory(slotIndex: Int){
        mc.displayGuiScreen(GuiInventory(mc.thePlayer))
        val windowId = GuiInventory(mc.thePlayer).inventorySlots.windowId
        var itemStack = mc.thePlayer.inventory.mainInventory[slotIndex]
        mc.netHandler.addToSendQueue(C0EPacketClickWindow(windowId, slotIndex,0,0,itemStack,0))
        itemStack = mc.thePlayer.inventory.mainInventory[mc.thePlayer.inventory.currentItem]
        mc.netHandler.addToSendQueue(C0EPacketClickWindow(windowId, mc.thePlayer.inventory.currentItem + 36,0,0,itemStack,0))
        itemStack = mc.thePlayer.inventory.mainInventory[slotIndex]
        mc.netHandler.addToSendQueue(C0EPacketClickWindow(windowId, slotIndex,0,0,itemStack,0))
        mc.thePlayer.closeScreen()
    }


    /**
     * @link https://stackoverflow.com/a/47925649
     */
    @Throws(IOException::class)
    fun getJavaRuntime(): String {
        val os = System.getProperty("os.name")
        val java = "${System.getProperty("java.home")}${File.separator}bin${File.separator}${
            if (os != null && os.lowercase().startsWith("windows")) "java.exe" else "java"
        }"
        if (!File(java).isFile) {
            throw IOException("Unable to find suitable java runtime at $java")
        }
        return java
    }
    fun interpolateRotation(par1: Float, par2: Float, par3: Float): Float {
        var f: Float = par2 - par1
        while (f < -180.0f) {
            f += 360.0f
        }
        while (f >= 180.0f) {
            f -= 360.0f
        }
        return par1 + par3 * f
    }

    fun rotateCorpse(bat: EntityLivingBase, p_77043_2_: Float, p_77043_3_: Float, partialTicks: Float) {
        GlStateManager.rotate(180.0f - p_77043_3_, 0.0f, 1.0f, 0.0f)
        if (bat.deathTime > 0) {
            var f: Float = (bat.deathTime.toFloat() + partialTicks - 1.0f) / 20.0f * 1.6f
            f = MathHelper.sqrt_float(f)
            if (f > 1.0f) {
                f = 1.0f
            }
            GlStateManager.rotate(f * 90.0f, 0.0f, 0.0f, 1.0f)
        } else {
            val s = EnumChatFormatting.getTextWithoutFormattingCodes(bat.getName())
            if (s != null && s == "Dinnerbone" || s == "Grumm" && (bat !is EntityPlayer || (bat as EntityPlayer).isWearing(
                    EnumPlayerModelParts.CAPE
                ))
            ) {
                GlStateManager.translate(0.0f, bat.height + 0.1f, 0.0f)
                GlStateManager.rotate(180.0f, 0.0f, 0.0f, 1.0f)
            }
        }
    }
    fun getRenderPartialTicks(): Float {
        val minecraft = Minecraft.getMinecraft()
        try {
            val timerField = Minecraft::class.java.getDeclaredField("timer")
            timerField.isAccessible = true
            val timer = timerField.get(minecraft)
            val renderPartialTicksField = timer::class.java.getDeclaredField("renderPartialTicks")
            renderPartialTicksField.isAccessible = true
            return renderPartialTicksField.getFloat(timer)
        } catch (e: NoSuchFieldException) {
            // Handle the exception
        } catch (e: IllegalAccessException) {
            // Handle the exception
        }
        return 0.0f
    }

    @JvmStatic
    fun String.stripColor(): String = STRIP_COLOR_PATTERN.replace(this, "")

    fun File.ensureFile() = (parentFile.exists() || parentFile.mkdirs()) && createNewFile()
    }


