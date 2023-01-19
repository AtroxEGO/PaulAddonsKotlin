package me.atroxego.pauladdons.commands

import PaulAddons
import PaulAddons.Companion.mc
import gg.skytils.skytilsmod.utils.SBInfo
import me.atroxego.pauladdons.config.Config
import me.atroxego.pauladdons.config.PersistentSave
import me.atroxego.pauladdons.features.funnyFishing.FishingData
import me.atroxego.pauladdons.gui.LocationEditGui
import me.atroxego.pauladdons.utils.Utils.sendItemTags
import net.minecraft.command.CommandBase
import net.minecraft.command.ICommandSender
import net.minecraft.util.ChatComponentText

class PaulAddonsCommand : CommandBase() {
    override fun getCommandName() = "pauladdons"

    override fun getCommandAliases() = listOf("pa", "paul")

    override fun getCommandUsage(sender: ICommandSender?) = "/$commandName help"

    override fun getRequiredPermissionLevel() = 0

    override fun processCommand(sender: ICommandSender?, args: Array<out String>?) {
        if (args!!.isEmpty()) {
            PaulAddons.currentGui = Config.gui()
        }
        when (args[0].lowercase()) {
            "hud" -> PaulAddons.currentGui = LocationEditGui()
            "gui" -> PaulAddons.currentGui = LocationEditGui()
            "edit" -> PaulAddons.currentGui = LocationEditGui()
            "save" -> Config.writeData()
            "mode" -> mc.thePlayer.addChatMessage(ChatComponentText("mode: " + SBInfo.mode + " date: " + SBInfo.date + " time: " + SBInfo.time))
            "s" -> PersistentSave.markDirty<FishingData>()
//            "hud" -> PaulAddons.currentGui = GuiHudEditor()
            "sendtags" -> sendItemTags()
//            "hastags" -> itemHasTags()
//            "date" -> dateStuff()
//            "dateset" -> Utils.dateSet(args[1], args[2], args[3])
//            "repair" -> Utils.repair()
            else -> mc.thePlayer.addChatMessage(ChatComponentText(
                """
                    §9PaulAddons §f:: §aUsage:
                    §9PaulAddons §f:: §a/pa §f- §aOpens GUI
                    §9PaulAddons §f:: §a/pa hud §f- §aEdit GUI Locations
                    """.trimIndent()
            ))
        }
    }
}