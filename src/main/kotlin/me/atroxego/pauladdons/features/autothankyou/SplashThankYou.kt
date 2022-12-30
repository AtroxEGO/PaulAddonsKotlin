package me.atroxego.pauladdons.features.autothankyou

import PaulAddons.Companion.mc
import me.atroxego.pauladdons.config.Config
import me.atroxego.pauladdons.utils.Utils.stripColor
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

object SplashThankYou {
    var lastTimeThanked : Long = 0
    @SubscribeEvent
    fun onSplash(event: ClientChatReceivedEvent){
        if(!Config.autoThankYou) return
        if(System.currentTimeMillis() - lastTimeThanked < 20000) return
        val message = event.message.unformattedText.stripColor()
        if(message.startsWith("BUFF! You were splashed by",true)){
            val splasher = message.removePrefix("BUFF! You were splashed by ").split(" ")[0].stripColor()
            val messageToSend = Config.thankYouMessage.replace("[IGN]",splasher)
            mc.thePlayer.sendChatMessage(messageToSend)
            lastTimeThanked = System.currentTimeMillis()
        }
    }

}