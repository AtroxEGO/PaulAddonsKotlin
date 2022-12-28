package me.atroxego.pauladdons.commands

import PaulAddons
import me.atroxego.pauladdons.config.Config
import me.atroxego.pauladdons.gui.LocationEditGui
import me.atroxego.pauladdons.utils.Utils
import me.atroxego.pauladdons.utils.Utils.dateStuff
import me.atroxego.pauladdons.utils.Utils.itemHasTags
import me.atroxego.pauladdons.utils.Utils.sendItemTags
import net.minecraft.command.CommandBase
import net.minecraft.command.ICommandSender

class ExampleCommand : CommandBase() {
    override fun getCommandName() = "pauladdons"

    override fun getCommandAliases() = listOf("pa", "paul")

    override fun getCommandUsage(sender: ICommandSender?) = "/$commandName"

    override fun getRequiredPermissionLevel() = 0

    override fun processCommand(sender: ICommandSender?, args: Array<out String>?) {
        if (args!!.isEmpty()) {
            PaulAddons.currentGui = Config.gui()
        }
        when (args[0].lowercase()) {
//            "hud" -> PaulAddons.currentGui = GuiHudEditor()
            "sendtags" -> sendItemTags()
            "hastags" -> itemHasTags()
            "date" -> dateStuff()
            "edit" -> PaulAddons.currentGui = LocationEditGui()
            "dateset" -> Utils.dateSet(args[1], args[2], args[3])
            "repair" -> Utils.repair()
        }
    }
}