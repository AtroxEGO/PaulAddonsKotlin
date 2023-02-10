package me.atroxego.pauladdons.utils

import me.atroxego.pauladdons.features.Feature
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import kotlin.math.pow

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
