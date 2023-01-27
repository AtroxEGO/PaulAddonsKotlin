package me.atroxego.pauladdons.hooks.render

import me.atroxego.pauladdons.events.impl.RenderEntityModelEvent
import net.minecraft.client.model.ModelBase
import net.minecraft.entity.EntityLivingBase
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

fun onRenderEntityModel(entity: EntityLivingBase, model: ModelBase, partialTicks: Float, ci: CallbackInfo) {
    if (RenderEntityModelEvent(entity, model,partialTicks).postAndCatch()) ci.cancel()
}