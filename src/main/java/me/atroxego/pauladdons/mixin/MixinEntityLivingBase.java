package me.atroxego.pauladdons.mixin;

import me.atroxego.pauladdons.hooks.render.EntityLivingBaseHook;
import me.atroxego.pauladdons.hooks.render.ExtensionEntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityLivingBase.class)
public abstract class MixinEntityLivingBase extends Entity implements ExtensionEntityLivingBase {

    @Unique
    private final EntityLivingBaseHook hook = new EntityLivingBaseHook((EntityLivingBase) (Object) this);

    public MixinEntityLivingBase(World worldIn) {
        super(worldIn);
    }

    @Inject(method = "isChild", at = @At("HEAD"), cancellable = true)
    private void setChildState(CallbackInfoReturnable<Boolean> cir) {
        hook.isChild(cir);
    }

    @NotNull
    @Override
    public EntityLivingBaseHook getSkytilsHook() {
        return hook;
    }
}
