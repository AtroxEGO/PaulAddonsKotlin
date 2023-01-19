package me.atroxego.pauladdons.hooks.network

import me.atroxego.pauladdons.events.impl.PacketEvent
import net.minecraft.network.Packet
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

fun onSendPacket(packet: Packet<*>, ci: CallbackInfo) {
    if (PacketEvent.SendEvent(packet).postAndCatch()) ci.cancel()
}