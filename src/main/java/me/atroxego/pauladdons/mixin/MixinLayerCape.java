/*
 * Skytils - Hypixel Skyblock Quality of Life Mod
 * Copyright (C) 2022 Skytils
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.atroxego.pauladdons.mixin;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerCape;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static me.atroxego.pauladdons.utils.core.Cosmetics.getCustomCape;

@Mixin(LayerCape.class)
public abstract class MixinLayerCape implements LayerRenderer<AbstractClientPlayer> {
    @Shadow
    @Final
    private RenderPlayer playerRenderer;

    @Inject(method = "doRenderLayer(Lnet/minecraft/client/entity/AbstractClientPlayer;FFFFFFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;translate(FFF)V", shift = At.Shift.BEFORE))
    private void scaleChild(AbstractClientPlayer entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale, CallbackInfo ci) {
        if (this.playerRenderer.getMainModel().isChild) {
            GlStateManager.scale(0.5, 0.5, 0.5);
            GlStateManager.translate(0.0F, 24.0F * scale, 0.0F);
        }
//        this.playerRenderer.bindTexture(new ResourceLocation("pauladdons/cape.png"));
    }

//    @Inject(method = "doRenderLayer(Lnet/minecraft/client/entity/AbstractClientPlayer;FFFFFFF)V", at = @At("HEAD"), cancellable = true)
    private void renderCustomCape(AbstractClientPlayer entitylivingbaseIn, float f, float g, float partialTicks, float h, float i, float j, float scale, CallbackInfo ci){

        ResourceLocation textureLocation = getCustomCape(entitylivingbaseIn.getName());
        if (textureLocation == null) return;
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.playerRenderer.bindTexture(textureLocation);

//        this.playerRenderer.bindTexture(new ResourceLocation("pauladdons/cape.png"));

        GlStateManager.pushMatrix();
        if (this.playerRenderer.getMainModel().isChild) {
            GlStateManager.scale(0.5, 0.5, 0.5);
            GlStateManager.translate(0.0F, 24.0F * scale, 0.2F);
        } else {
            GlStateManager.translate(0.0F, 0.0F, 0.125F);
        }
        double d = entitylivingbaseIn.prevChasingPosX + (entitylivingbaseIn.chasingPosX - entitylivingbaseIn.prevChasingPosX) * (double)partialTicks - (entitylivingbaseIn.prevPosX + (entitylivingbaseIn.posX - entitylivingbaseIn.prevPosX) * (double)partialTicks);
        double e = entitylivingbaseIn.prevChasingPosY + (entitylivingbaseIn.chasingPosY - entitylivingbaseIn.prevChasingPosY) * (double)partialTicks - (entitylivingbaseIn.prevPosY + (entitylivingbaseIn.posY - entitylivingbaseIn.prevPosY) * (double)partialTicks);
        double k = entitylivingbaseIn.prevChasingPosZ + (entitylivingbaseIn.chasingPosZ - entitylivingbaseIn.prevChasingPosZ) * (double)partialTicks - (entitylivingbaseIn.prevPosZ + (entitylivingbaseIn.posZ - entitylivingbaseIn.prevPosZ) * (double)partialTicks);
        float l = entitylivingbaseIn.prevRenderYawOffset + (entitylivingbaseIn.renderYawOffset - entitylivingbaseIn.prevRenderYawOffset) * partialTicks;
        double m = (double) MathHelper.sin(l * 3.1415927F / 180.0F);
        double n = (double)(-MathHelper.cos(l * 3.1415927F / 180.0F));
        float o = (float)e * 10.0F;
        o = MathHelper.clamp_float(o, -6.0F, 32.0F);
        float p = (float)(d * m + k * n) * 100.0F;
        float q = (float)(d * n - k * m) * 100.0F;
        if (p < 0.0F) {
            p = 0.0F;
        }

        float r = entitylivingbaseIn.prevCameraYaw + (entitylivingbaseIn.cameraYaw - entitylivingbaseIn.prevCameraYaw) * partialTicks;
        o += MathHelper.sin((entitylivingbaseIn.prevDistanceWalkedModified + (entitylivingbaseIn.distanceWalkedModified - entitylivingbaseIn.prevDistanceWalkedModified) * partialTicks) * 6.0F) * 32.0F * r;
        if (entitylivingbaseIn.isSneaking()) {
            o += 25.0F;
        }

        GlStateManager.rotate(6.0F + p / 2.0F + o, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(q / 2.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(-q / 2.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
        this.playerRenderer.getMainModel().renderCape(0.0625F);
        GlStateManager.popMatrix();
        ci.cancel();
    }
}
