package me.atroxego.pauladdons.features.autothankyou

import PaulAddons.Companion.mc
import me.atroxego.pauladdons.config.Config
import me.atroxego.pauladdons.utils.Utils.stripColor
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent

object SplashThankYou {
    var lastTimeThanked : Long = 0
    var ticksWithoutMessage = 0
    var countTicks = false
    var messageToSend : String = ""
    @SubscribeEvent
    fun onSplash(event: ClientChatReceivedEvent){
        if(!Config.autoThankYou) return
        val message = event.message.unformattedText.stripColor()
        if(message.startsWith("BUFF! You were splashed by",true)){
            countTicks = true
            ticksWithoutMessage = 0
            val splasher = message.removePrefix("BUFF! You were splashed by ").split(" ")[0].stripColor()
            messageToSend = Config.thankYouMessage.replace("[IGN]",splasher)
        }

    }
    @SubscribeEvent
    fun tickTimer(event: TickEvent.PlayerTickEvent){
        if (countTicks) ticksWithoutMessage++
        if (ticksWithoutMessage > 320){
            mc.thePlayer.sendChatMessage("/ac $messageToSend")
            lastTimeThanked = System.currentTimeMillis()
            countTicks = false
            ticksWithoutMessage = 0
        }
    }

}