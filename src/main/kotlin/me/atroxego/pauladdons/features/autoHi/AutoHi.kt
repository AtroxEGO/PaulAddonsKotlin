package me.atroxego.pauladdons.features.autoHi

import PaulAddons.Companion.mc
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
            if (messageIncoming.startsWith("Friend > ")) {
                if (messageIncoming.contains(ign.plus(" joined."))){
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
