package me.atroxego.pauladdons.mixin;

import me.atroxego.pauladdons.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderItem.class)
public abstract class MixinRenderItem {

    @Inject(method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/resources/model/IBakedModel;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/RenderItem;renderModel(Lnet/minecraft/client/resources/model/IBakedModel;Lnet/minecraft/item/ItemStack;)V", shift = At.Shift.AFTER), cancellable = true)
    void stopGlint(ItemStack stack, IBakedModel model, CallbackInfo ci){
        if (Config.INSTANCE.getRemoveArmorGlint()){
            for (ItemStack itemStack : Minecraft.getMinecraft().thePlayer.inventory.armorInventory) {
                if (itemStack == stack) stack.getTagCompound().removeTag("ench");
            }
        }
    }
}
