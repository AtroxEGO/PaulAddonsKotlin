package me.atroxego.pauladdons.commands

import PaulAddons
import PaulAddons.Companion.mc
import PaulAddons.Companion.prefix
import me.atroxego.pauladdons.config.Config
import me.atroxego.pauladdons.features.dungeons.Jerry.toggleJerry
import me.atroxego.pauladdons.features.kuudra.ChaosmiteCounter.chaosCounter
import me.atroxego.pauladdons.features.other.PetSwapper
import me.atroxego.pauladdons.features.other.Ping
import me.atroxego.pauladdons.features.other.Ping.sendPing
import me.atroxego.pauladdons.gui.LocationEditGui
import me.atroxego.pauladdons.utils.PlayerRotation
import me.atroxego.pauladdons.utils.SBInfo
import me.atroxego.pauladdons.utils.SBInfo.onSkyblock
import me.atroxego.pauladdons.utils.Utils.addMessage
import me.atroxego.pauladdons.utils.Utils.scoreboardData
import net.minecraft.command.CommandBase
import net.minecraft.command.ICommandSender
import net.minecraft.util.BlockPos
import net.minecraft.util.ChatComponentText
import net.minecraftforge.common.MinecraftForge

class PaulAddonsCommand : CommandBase() {
    override fun getCommandName() = "pauladdons"

    override fun getCommandAliases() = listOf("pa", "paul")

    override fun getCommandUsage(sender: ICommandSender?) = "/$commandName help"

    override fun getRequiredPermissionLevel() = 0

    override fun addTabCompletionOptions(sender: ICommandSender, args: Array<String>, pos: BlockPos): List<String>? {
        return if (args.size == 1) getListOfStringsMatchingLastWord(args, "jerry","pet","edit","config","help")
        else null
    }

    override fun processCommand(sender: ICommandSender?, args: Array<out String>?) {
        if (args!!.isEmpty()) {
            PaulAddons.currentGui = Config.gui()
            return
        }
        when (args[0].lowercase()) {
            "hud" -> PaulAddons.currentGui = LocationEditGui()
            "gui" -> PaulAddons.currentGui = LocationEditGui()
            "edit" -> PaulAddons.currentGui = LocationEditGui()
            "save" -> Config.writeData()
            "pet" -> {
                if (args.size < 2) {
                    addMessage("$prefix Usage: /pa pet [Pet Index or Name]")
                    return
                }
                mc.thePlayer.sendChatMessage("/pets")
                MinecraftForge.EVENT_BUS.register(PetSwapper(args[1]))

            }
            "ping" -> {
                addMessage("${Ping.ping}")
                sendPing()
            }
            "dev" -> {
                PaulAddons.devMode = !PaulAddons.devMode
                addMessage("$prefix Developer mode: ${PaulAddons.devMode}")
            }
            "counter" -> chaosCounter = !chaosCounter
            "mode" -> mc.thePlayer.addChatMessage(ChatComponentText("mode: " + SBInfo.mode + " inSkyblock: " + onSkyblock))
            "sb" -> scoreboardData()
            "jerry" -> toggleJerry()
            "lookat" -> PlayerRotation(PlayerRotation.Rotation(args[1].toFloat(), args[2].toFloat()), 0)
            else -> mc.thePlayer.addChatMessage(ChatComponentText(
                """
                    §9PaulAddons §f:: §aUsage:
                    §9PaulAddons §f:: §a/pa §f- §aOpens GUI
                    §9PaulAddons §f:: §a/pa jerry §f- §aToggles Jerry Knockback
                    §9PaulAddons §f:: §a/pa hud §f- §aEdit GUI Locations
                    """.trimIndent()
            ))
        }
    }
}