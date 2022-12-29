package me.atroxego.pauladdons.features.autothankyou

import PaulAddons.Companion.mc
import me.atroxego.pauladdons.config.Config
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

object SplashThankYou {
    var lastTimeThanked : Long = 0
    @SubscribeEvent
    fun onSplash(event: ClientChatReceivedEvent){
        if(!Config.autoThankYou) return
        if(System.currentTimeMillis() - lastTimeThanked < 20000) return
        val message = event.message.unformattedText
        if (message.startsWith("BUFF! You were splashed by ")){
            val splasher = message.removePrefix("BUFF! You were splashed by ").split(" ")[0]
            val messageToSend = Config.thankYouMessage.replace("[IGN]",splasher)
            mc.thePlayer.sendChatMessage(messageToSend)
            lastTimeThanked = System.currentTimeMillis()
        }
    }

}