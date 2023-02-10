/*
 * Skytils - Hypixel Skyblock Quality of Life Mod
 * Copyright (C) 2022 Skytils
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

package me.atroxego.pauladdons.utils

import PaulAddons.Companion.mc
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import kotlin.math.pow

class PlayerRotationUtil(rotationData : PlayerRotation.RotationData) {

    var startRot = rotationData.startRot
    var endRot = rotationData.endRotation
    var startTime = rotationData.startTime
    var endTime = rotationData.endTime
    @Volatile
    var done = rotationData.done



    @SubscribeEvent
    fun renderWorldLast(event: RenderWorldLastEvent){
        if (mc.thePlayer == null || mc.theWorld == null) return MinecraftForge.EVENT_BUS.unregister(this)

        if (System.currentTimeMillis() <= endTime) {
            mc.thePlayer.rotationYaw = interpolate(startRot.yaw, endRot.yaw)
            mc.thePlayer.rotationPitch = interpolate(startRot.pitch, endRot.pitch)
        } else if (!done) {
            mc.thePlayer.rotationYaw = endRot.yaw
            mc.thePlayer.rotationPitch = endRot.pitch
            done = true
            MinecraftForge.EVENT_BUS.unregister(this)
        }
    }

    private fun interpolate(start: Float, end: Float): Float {
        val spentMillis = (System.currentTimeMillis() - startTime).toFloat()
        val relativeProgress = spentMillis / (endTime - startTime).toFloat()
        return (end - start) * easeOutCubic(relativeProgress.toDouble()) + start
    }

    private fun easeOutCubic(number: Double): Float {
        return (1.0 - (1.0 - number).pow(3.0)).toFloat()
    }
}