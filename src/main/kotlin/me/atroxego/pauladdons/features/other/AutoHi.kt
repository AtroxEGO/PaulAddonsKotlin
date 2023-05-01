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


package me.atroxego.pauladdons.features.other

import me.atroxego.pauladdons.PaulAddons.Companion.mc
import me.atroxego.pauladdons.config.Config
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import java.time.LocalDate
import java.time.LocalDateTime

object AutoHi {

    var lastMessage = hashMapOf<String,Long>()
    @SubscribeEvent
    fun listenForFriendsJoin(event: ClientChatReceivedEvent){
        if (!Config.autoFriendHi && !Config.autoGuildHi) return
        val messageIncoming = event.message.unformattedText
        val messageOutcoming = Config.autoGuildHiCustomMessage
        if (messageIncoming.contains("You are playing on profile:") && Config.autoGuildHi){
            if (Config.autoGuildHiFrequency == 0){
                val day = LocalDate.now().dayOfMonth
                val hour = LocalDateTime.now().hour
                if (day != Config.lastGuildHi && hour > 4){

                    mc.thePlayer.sendChatMessage("/gc $messageOutcoming")
                    Config.lastGuildHi = day
                    }
            } else {
                mc.thePlayer.sendChatMessage("/gc $messageOutcoming")
                Config.lastGuildHi = LocalDate.now().dayOfMonth
            }
        }

        if (Config.autoHiFriends == "" || !Config.autoFriendHi) return
        for (ign in Config.autoHiFriends.split(", ")) {
            if (messageIncoming.startsWith("Friend > ") || messageIncoming.startsWith("Guild > ")) {
                if (messageIncoming.contains(ign.plus(" joined."),true)){
                    var command : String
                    if (lastMessage[ign] == null || System.currentTimeMillis() - lastMessage[ign]!! > Config.autoFriendHiCooldown * 1000){
                        if (Config.autoFriendHiType == 0){
                            if(Config.autoHiCustomCommand == ""){
                                command = "/msg $ign Hi $ign!"
                            } else{
                                command = Config.autoHiCustomCommand.replace("[IGN]", ign)
                            }
                        }else{
                            command = "/boop $ign"
                        }
                        mc.thePlayer.sendChatMessage(command)
                        lastMessage[ign] = System.currentTimeMillis()
                    }
                }
            }
        }
    }
}
