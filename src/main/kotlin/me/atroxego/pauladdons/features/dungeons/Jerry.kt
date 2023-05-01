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


package me.atroxego.pauladdons.features.dungeons

import me.atroxego.pauladdons.PaulAddons.Companion.prefix
import me.atroxego.pauladdons.config.Config
import me.atroxego.pauladdons.events.impl.PacketEvent
import me.atroxego.pauladdons.features.Feature
import me.atroxego.pauladdons.utils.Utils.addMessage
import me.atroxego.pauladdons.utils.Utils.stripColor
import net.minecraft.network.play.server.S12PacketEntityVelocity
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

object Jerry : Feature() {

    @SubscribeEvent
    fun onPacket(event: PacketEvent.ReceiveEvent){
        if (!Config.jerryKB) return
        if (mc.thePlayer == null) return
        val heldItem = mc.thePlayer.heldItem
        if (heldItem != null && heldItem.displayName.stripColor().contains("Bonzo's Staff")) return
        try {
            if (event.packet.toString().contains("S12PacketEntityVelocity")){
                if ((event.packet as S12PacketEntityVelocity).entityID == mc.thePlayer.entityId){
                    mc.thePlayer.motionY = event.packet.motionY/8000.0
                    event.isCanceled = true
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            printdev("${prefix} Failed to apply Jerry Knockback")
        }
        }

    fun toggleJerry(){
        Config.jerryKB = !Config.jerryKB
        addMessage(if (Config.jerryKB) "$prefix Jerry: §aOn" else "$prefix Jerry: §cOff")
        mc.thePlayer.playSound("random.orb",1.0f, 0.7f)
    }
}