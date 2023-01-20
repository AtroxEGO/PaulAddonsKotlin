package me.atroxego.pauladdons.features.dungeons

import PaulAddons.Companion.mc
import me.atroxego.pauladdons.config.Config
import me.atroxego.pauladdons.utils.Utils
import net.minecraft.init.Blocks
import net.minecraft.init.Items
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.InputEvent
import org.lwjgl.input.Mouse

object BetterStonk {

    @SubscribeEvent
    fun onPickaxeMine(event: InputEvent.MouseInputEvent){
        if (!Config.betterStonk) return
        if (!Utils.inDungeon) return
        if (Config.betterStonkShiftOnly) {
            if (!mc.thePlayer.isSneaking) return
        }
        val heldStack = mc.thePlayer.heldItem ?: return
        val heldItem = heldStack.item
        if (Mouse.isButtonDown(0)) {
            if (heldItem == Items.golden_pickaxe || heldItem == Items.diamond_pickaxe || heldItem == Items.iron_pickaxe || heldItem == Items.iron_pickaxe || heldItem == Items.stone_pickaxe || heldItem == Items.wooden_pickaxe){
                createGhostBlock()
            }
        }
    }

    fun createGhostBlock(){
        val raytrace = mc.thePlayer.rayTrace(5.0,1.0f)
        if (raytrace != null){
            val block = mc.thePlayer.worldObj.getChunkFromBlockCoords(raytrace.blockPos).getBlock(raytrace.blockPos)
            if (block != Blocks.chest && block != Blocks.trapped_chest && block != Blocks.skull && block != Blocks.lever && block != Blocks.hopper) mc.thePlayer.worldObj.setBlockToAir(raytrace.blockPos)
            else return
        }else return
    }
}