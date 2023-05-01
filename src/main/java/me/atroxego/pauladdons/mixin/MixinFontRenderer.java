package me.atroxego.pauladdons.mixin;

import me.atroxego.pauladdons.utils.core.Cosmetics;
import net.minecraft.client.gui.FontRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = FontRenderer.class, priority = 2000)
public abstract class MixinFontRenderer {

    @ModifyVariable(method = "drawString(Ljava/lang/String;FFIZ)I", at = @At(value = "HEAD"), argsOnly = true)
    public String drawString(String text) {
        return Cosmetics.getCustomNicks(text);
    }
}
