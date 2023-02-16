package me.atroxego.pauladdons.mixin;

import me.atroxego.pauladdons.hooks.controller.PlayerAttackEntityHookKt;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerControllerMP.class)
public abstract class MixinPlayerAttackEntity {

    @Inject(method = "attackEntity", at = @At("HEAD"), cancellable = true)
    private void onPlayerAttackEntity(EntityPlayer playerIn, Entity targetEntity, CallbackInfo ci){
        PlayerAttackEntityHookKt.onPlayerEntityAttack(playerIn,targetEntity,ci);
    }
}
