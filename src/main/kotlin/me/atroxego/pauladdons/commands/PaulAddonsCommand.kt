package me.atroxego.pauladdons.commands

import PaulAddons
import PaulAddons.Companion.mc
import me.atroxego.pauladdons.config.Config
import me.atroxego.pauladdons.gui.LocationEditGui
import me.atroxego.pauladdons.utils.SBInfo
import me.atroxego.pauladdons.utils.SBInfo.onSkyblock
import me.atroxego.pauladdons.utils.Utils.scoreboardData
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
            "mode" -> mc.thePlayer.addChatMessage(ChatComponentText("mode: " + SBInfo.mode + " inSkyblock: " + onSkyblock))
            "sb" -> scoreboardData()
//                    + " date: " +
//                    SBInfo.date.split(" ")[2].dropLast(SBInfo.date.split(" ")[2].length-2)
//                    + " time: " +
//                    SBInfo.time.split(":")[0] + " "+SBInfo.time.split(":")[1].dropLast(2)
//            ))
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