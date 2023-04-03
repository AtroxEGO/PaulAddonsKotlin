package me.atroxego.pauladdons.mixin;

import com.moandjiezana.toml.Toml;
import net.minecraft.block.Block;
import net.minecraft.block.BlockMushroom;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;


@Mixin(BlockMushroom.class)
public class MixinBlockMushroom extends Block {

    public MixinBlockMushroom(Material p_i46399_1_, MapColor p_i46399_2_) {
        super(p_i46399_1_, p_i46399_2_);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    public void changeHitbox(CallbackInfo ci) {
            Toml toml = new Toml().read(new File(Minecraft.getMinecraft().mcDataDir + "/config/pauladdons","config.toml"));
            boolean shouldChangeHitbox = toml.getBoolean("miscellaneous.better_farming_hitboxes");
            if (shouldChangeHitbox){
                this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
            }
    }

}
