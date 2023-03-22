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


package me.atroxego.pauladdons.features.kuudra

import me.atroxego.pauladdons.config.Config
import me.atroxego.pauladdons.events.impl.PacketEvent
import me.atroxego.pauladdons.render.DisplayNotification.displayNotification
import me.atroxego.pauladdons.utils.Utils.stripColor
import net.minecraft.network.play.server.S45PacketTitle
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent

object Dropships {
    var dropshipTimeMessage = -1L
    var timeOffset = 44000

    @SubscribeEvent
    fun onChat(event: PacketEvent.ReceiveEvent){
        if (!Config.dropshipNotification) return
        if (event.packet !is S45PacketTitle) return
        if (event.packet.type == null) return
        if (event.packet.type.name != "TITLE") return
        if (event.packet.message == null) return
        if (event.packet.message.unformattedText.stripColor() == "DROPSHIP INCOMING") dropshipTimeMessage = System.currentTimeMillis() + timeOffset
    }

    @SubscribeEvent
    fun onTick(event: PlayerTickEvent){
        if (dropshipTimeMessage == -1L) return
        if (dropshipTimeMessage - System.currentTimeMillis() < 0){
            displayNotification("§cDropship", 1500,true)
            dropshipTimeMessage = -1L
        }
    }

    @SubscribeEvent
    fun onWorldChange(event: WorldEvent.Load){
        dropshipTimeMessage = -1L
    }
}