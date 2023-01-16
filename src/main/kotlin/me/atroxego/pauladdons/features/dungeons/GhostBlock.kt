package me.atroxego.pauladdons.features.dungeons

import PaulAddons.Companion.mc
import net.minecraft.init.Blocks

object GhostBlock {
    fun createGhostBlock(){
        val raytrace = mc.thePlayer.rayTrace(5.0,1.0f)
        if (raytrace != null){
            val block = mc.thePlayer.worldObj.getChunkFromBlockCoords(raytrace.blockPos).getBlock(raytrace.blockPos)
            if (block != Blocks.chest && block != Blocks.trapped_chest && block != Blocks.skull && block != Blocks.lever && block != Blocks.hopper) mc.thePlayer.worldObj.setBlockToAir(raytrace.blockPos)
            else return
        }else return
    }
}