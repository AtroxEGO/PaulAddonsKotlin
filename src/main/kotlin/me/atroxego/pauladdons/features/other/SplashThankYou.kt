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
        if (ticksWithoutMessage > 4000){
            lastTimeThanked = System.currentTimeMillis()
            countTicks = false
            ticksWithoutMessage = 0
            mc.thePlayer.sendChatMessage("/ac $messageToSend")
        }
    }

}