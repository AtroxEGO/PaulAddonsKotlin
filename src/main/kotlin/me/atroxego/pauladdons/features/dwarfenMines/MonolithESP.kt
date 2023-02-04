package me.atroxego.pauladdons.features.dwarfenMines

import PaulAddons.Companion.mc
import PaulAddons.Companion.prefix
import me.atroxego.pauladdons.config.Config
import me.atroxego.pauladdons.render.RenderUtils.drawBox
import me.atroxego.pauladdons.utils.SBInfo
import me.atroxego.pauladdons.utils.Utils.addMessage
import net.minecraft.init.Blocks
import net.minecraft.util.BlockPos
import net.minecraft.util.Vec3
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import java.awt.Color

object MonolithESP {

    private val monolithBlockCoords = listOf(
        Vec3(-15.5,236.0,-92.5),
        Vec3(49.5,202.0,-162.5),
        Vec3(56.5,214.0,-25.5),
        Vec3(128.5,187.0,58.5),
        Vec3(150.5,196.0,190.5),
        Vec3(61.5,204.0,181.5),
        Vec3(91.5,187.0,131.5),
        Vec3(77.5,160.0,162.5),
        Vec3(-10.5,162.0,109.5),
        Vec3(1.5,183.0,25.5),
        Vec3(1.5,170.0,0.0),
        Vec3(-94.5,201.0,-30.5),
        Vec3(-91.5,221.0,-53.5),
        Vec3(-64.5,206.0,-63.5),
        Vec3(0.5,170.0,0.5)
    )

    var lastTimeChecked : Long = 0
    var monolithCoords : Vec3? = null

    @SubscribeEvent
    fun worldRender(event: RenderWorldLastEvent){
        if (SBInfo.mode != "mining_3") return
        if (!Config.monolithESP) return
        if (System.currentTimeMillis()/1000 - lastTimeChecked > 1) {
            if (monolithCoords != null) {
                val actualCoords = Vec3(monolithCoords!!.xCoord, monolithCoords!!.yCoord + 1.5, monolithCoords!!.zCoord)
                if (mc.theWorld.getChunkFromBlockCoords(BlockPos(actualCoords)).getBlock(BlockPos(actualCoords)) == Blocks.dragon_egg) {
                    drawBox(monolithCoords!!, Color(200,0,200,255), event.partialTicks)
                    return
                }
            }
//                addMessage("Checking For Monoliths")
                lastTimeChecked = System.currentTimeMillis() / 1000
                for (possibleBlock in monolithBlockCoords) {
                    for (offsetX in -5..5) {
                        for (offsetZ in -5..5) {
                            val blockPos = BlockPos(
                                possibleBlock.xCoord + offsetX,
                                possibleBlock.yCoord,
                                possibleBlock.zCoord + offsetZ
                            )
                            if (mc.theWorld.getChunkFromBlockCoords(BlockPos(blockPos))
                                    .getBlock(BlockPos(blockPos)) == Blocks.dragon_egg
                            ) {
                                addMessage("$prefix Found Monolith!")
                                monolithCoords = Vec3(
                                    possibleBlock.xCoord + offsetX,
                                    possibleBlock.yCoord - 1.5,
                                    possibleBlock.zCoord + offsetZ
                                )
                                break
                            }
                        }
                    }
                }
            }
        if (monolithCoords == null) return
        val actualCoords = Vec3(monolithCoords!!.xCoord, monolithCoords!!.yCoord+1.5, monolithCoords!!.zCoord)
        if (mc.thePlayer.worldObj.getChunkFromBlockCoords(BlockPos(actualCoords)).getBlock(BlockPos(actualCoords)) != Blocks.dragon_egg) return
        drawBox(monolithCoords!!, Color(200,0,200,255),event.partialTicks)
    }

    @SubscribeEvent
    fun onWorldChange(event: WorldEvent.Load){
        lastTimeChecked = 0
    }
}