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

import me.atroxego.pauladdons.features.Feature
import net.minecraftforge.common.MinecraftForge

class PlayerRotation(rotation: Rotation, time: Long) : Feature(){
    var startRot = Rotation(0f, 0f)
    var endRot = Rotation(0f, 0f)
    var startTime = 0L
    var endTime = 0L
    @Volatile
    var done = true

    init {
        done = false
        startRot = Rotation(mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch)
        endRot = rotation
        startTime = System.currentTimeMillis()
        endTime = System.currentTimeMillis() + time
//        printdev("$rotation")
        MinecraftForge.EVENT_BUS.register(PlayerRotationUtil(RotationData(startRot,endRot,startTime,endTime,done)))
//        printdev("Rotation: $rotation, Time: $time")
//        mc.thePlayer.rotationYaw = rotation.yaw
    }

    data class Rotation(var yaw: Float, var pitch: Float)
    data class RotationData(var startRot : Rotation,var endRotation : Rotation, var startTime : Long, var endTime: Long, var done : Boolean)
}
