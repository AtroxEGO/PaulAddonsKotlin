package me.atroxego.pauladdons.mixin;

import me.atroxego.pauladdons.hooks.render.FishEntityHooked;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityFishHook;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityFishHook.class)
public abstract class MixinEntityFishHook {

    @Shadow public Entity caughtEntity;

    //    @Inject(method = "doRenderEntity(Lnet/minecraft/entity/Entity;DDDFFZ)Z", at = @At("HEAD"), cancellable = true)
    @Inject(method = "onUpdate", at = @At("RETURN"))
    public void onHook(CallbackInfo ci) {
        if (caughtEntity != null) {
//            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(caughtEntity.getName()));
            FishEntityHooked.fishEntityHooked(caughtEntity);
        }
//        RenderEntityHookKt.onRenderEntityModel((EntityLivingBase) entity,((IMixinRendererLivingEntity) render).getMainModel(),partialTicks, cir);
    }
}