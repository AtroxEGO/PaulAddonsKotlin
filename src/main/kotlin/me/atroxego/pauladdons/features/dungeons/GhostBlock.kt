package me.atroxego.pauladdons.features.dungeons

import PaulAddons.Companion.mc

object GhostBlock {
    fun createGhostBlock(){
        val raytrace = mc.thePlayer.rayTrace(5.0,1.0f)
        if (raytrace != null)mc.theWorld.setBlockToAir(raytrace.blockPos)
    }
}