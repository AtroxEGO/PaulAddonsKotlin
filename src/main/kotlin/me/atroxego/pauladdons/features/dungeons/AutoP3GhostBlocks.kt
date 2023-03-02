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

import PaulAddons.Companion.mc
import me.atroxego.pauladdons.config.Config
import me.atroxego.pauladdons.utils.Utils
import me.atroxego.pauladdons.utils.Utils.inDungeon
import net.minecraft.util.BlockPos
import net.minecraft.util.Vec3
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent

object AutoP3GhostBlocks {

    var lastTimeGhostBLocksDone : Long = 0

    @SubscribeEvent
    fun onWorldLoad(event: WorldEvent.Load){
        lastTimeGhostBLocksDone = 0
    }

    val ghostBlockCoords = listOf<Vec3>(
        Vec3(88.5,167.0,41.5),
        Vec3(89.5,167.0,41.5),
        Vec3(91.5,167.0,41.5),
        Vec3(92.5,167.0,41.5),
        Vec3(93.5,167.0,41.5),
        Vec3(94.5,167.0,41.5),
        Vec3(95.5,167.0,41.5),
        Vec3(96.5,167.0,41.5),
        Vec3(96.5,167.0,40.5),
        Vec3(95.5,167.0,40.5),
        Vec3(94.5,167.0,40.5),
        Vec3(93.5,167.0,40.5),
        Vec3(92.5,167.0,40.5),
        Vec3(90.5,167.0,41.5),
        Vec3(91.5,167.0,40.5),
        Vec3(91.5,166.0,40.5),
        Vec3(92.5,166.0,40.5),
        Vec3(93.5,166.0,40.5),
        Vec3(94.5,166.0,40.5),
        Vec3(95.5,166.0,40.5),
        Vec3(91.5,166.0,41.5),
        Vec3(92.5,166.0,41.5),
        Vec3(93.5,166.0,41.5),
        Vec3(94.5,166.0,41.5),
        Vec3(95.5,166.0,41.5),
        Vec3(54.5,64.0,78.5),
        Vec3(54.5,64.0,77.5),
        Vec3(54.5,64.0,76.5),
        Vec3(54.5,64.0,75.5),
        Vec3(54.5,64.0,74.5),
    )


    @SubscribeEvent
    fun onTick(event: ClientTickEvent){
        if (!Config.autoP3P5GhostBlocks) return
        if (mc.thePlayer == null || mc.theWorld == null) return
        if (!inDungeon) return
        if (System.currentTimeMillis()/1000 - lastTimeGhostBLocksDone < 30) return
        try {
            if(!Utils.getScoreboardLines()[3].contains("F7") && !Utils.getScoreboardLines()[3].contains("M7")) return
            lastTimeGhostBLocksDone = System.currentTimeMillis()/1000
            for (block in ghostBlockCoords){
                val blockPos = BlockPos(block)
                mc.thePlayer.worldObj.setBlockToAir(blockPos)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}