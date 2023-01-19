package me.atroxego.pauladdons.hooks.network

import io.netty.channel.ChannelHandlerContext
import me.atroxego.pauladdons.events.impl.PacketEvent
import net.minecraft.network.Packet
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

fun onReceivePacket(context: ChannelHandlerContext, packet: Packet<*>, ci: CallbackInfo) {
    if (PacketEvent.ReceiveEvent(packet).postAndCatch()) ci.cancel()
}