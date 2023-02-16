package me.atroxego.pauladdons.hooks.controller

import me.atroxego.pauladdons.events.impl.PlayerAttackEntityEvent
import me.atroxego.pauladdons.features.other.AutoDojo
import me.atroxego.pauladdons.features.other.AutoDojo.autoDojoToggle
import me.atroxego.pauladdons.features.other.AutoDojo.dojoType
import me.atroxego.pauladdons.utils.Utils.addMessage
import net.minecraft.entity.Entity
import net.minecraft.entity.monster.EntityZombie
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Items
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

fun onPlayerEntityAttack(playerIn: EntityPlayer, targetEntity : Entity,ci : CallbackInfo) {
    if (PlayerAttackEntityEvent(targetEntity).postAndCatch()) {
        addMessage("Hi")
        ci.cancel()
    }
    if (autoDojoToggle) {
        if (dojoType == AutoDojo.DojoType.FORCE){
            if (targetEntity !is EntityZombie) return
            val targetHelmet = targetEntity.getEquipmentInSlot(4) ?: return
                if (targetHelmet.item == Items.leather_helmet) ci.cancel()
        }
    }
}