/*
 *
 * MIT License
 *
 * Copyright (c) 2022 Cephetir
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package me.atroxego.pauladdons.render

import PaulAddons.Companion.mc
import me.atroxego.pauladdons.config.Config
import me.atroxego.pauladdons.mixin.IMixinRenderManager
import me.atroxego.pauladdons.mixin.IMixinRendererLivingEntity
import me.atroxego.pauladdons.utils.Utils.getRenderPartialTicks
import me.atroxego.pauladdons.utils.Utils.interpolateRotation
import me.atroxego.pauladdons.utils.Utils.rotateCorpse
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Gui
import net.minecraft.client.model.ModelBase
import net.minecraft.client.renderer.*
import net.minecraft.client.renderer.entity.layers.LayerArmorBase
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.item.EntityArmorStand
import net.minecraft.entity.monster.EntitySkeleton
import net.minecraft.item.ItemArmor
import net.minecraft.item.ItemStack
import net.minecraft.util.AxisAlignedBB
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11.glDisable
import org.lwjgl.opengl.GL11.glEnable


object RenderUtils {

    @JvmStatic
    fun renderItem(itemStack: ItemStack?, x: Int, y: Int) {
        RenderHelper.enableGUIStandardItemLighting()
        GlStateManager.enableDepth()
        mc.renderItem.renderItemAndEffectIntoGUI(itemStack, x, y)
    }
    fun renderBoundingBox(entity: Entity, color: Int) {
        var visible = false
        if (Minecraft.getMinecraft().thePlayer.canEntityBeSeen(entity)){ visible = true }
        if (Config.disableVisible && visible) {return}
        val rm = mc.renderManager as IMixinRenderManager
        val partialTicks = getRenderPartialTicks()
        val renderPosX = rm.renderPosX
        val renderPosY = rm.renderPosY
        val renderPosZ = rm.renderPosZ
        val x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks - renderPosX
        val y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks - renderPosY
        val z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks - renderPosZ
        val bbox = entity.entityBoundingBox
        var aabb = AxisAlignedBB(
            bbox.minX - entity.posX + x,
            bbox.minY - entity.posY + y,
            bbox.minZ - entity.posZ + z,
            bbox.maxX - entity.posX + x,
            bbox.maxY - entity.posY + y,
            bbox.maxZ - entity.posZ + z
        )
        if (entity is EntityArmorStand) aabb = aabb.expand(0.3, 1.0, 0.3)
        drawFilledBoundingBox(aabb, color)
    }

    fun drawFilledBoundingBox(aabb: AxisAlignedBB, color: Int) {
        GlStateManager.enableBlend()
        GlStateManager.disableDepth()
        GlStateManager.disableLighting()
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0)
        GlStateManager.disableTexture2D()
        val tessellator = Tessellator.getInstance()
        val worldrenderer = tessellator.worldRenderer
        val a = (color shr 24 and 255).toFloat() / 255.0f
        val r = (color shr 16 and 255).toFloat() / 255.0f
        val g = (color shr 8 and 255).toFloat() / 255.0f
        val b = (color and 255).toFloat() / 255.0f
        val opacity = 0.3f
        GlStateManager.color(r, g, b, a * opacity)
        worldrenderer.begin(7, DefaultVertexFormats.POSITION)
        worldrenderer.pos(aabb.minX, aabb.minY, aabb.minZ).endVertex()
        worldrenderer.pos(aabb.maxX, aabb.minY, aabb.minZ).endVertex()
        worldrenderer.pos(aabb.maxX, aabb.minY, aabb.maxZ).endVertex()
        worldrenderer.pos(aabb.minX, aabb.minY, aabb.maxZ).endVertex()
        tessellator.draw()
        worldrenderer.begin(7, DefaultVertexFormats.POSITION)
        worldrenderer.pos(aabb.minX, aabb.maxY, aabb.maxZ).endVertex()
        worldrenderer.pos(aabb.maxX, aabb.maxY, aabb.maxZ).endVertex()
        worldrenderer.pos(aabb.maxX, aabb.maxY, aabb.minZ).endVertex()
        worldrenderer.pos(aabb.minX, aabb.maxY, aabb.minZ).endVertex()
        tessellator.draw()
        GlStateManager.color(r, g, b, a * opacity)
        worldrenderer.begin(7, DefaultVertexFormats.POSITION)
        worldrenderer.pos(aabb.minX, aabb.minY, aabb.maxZ).endVertex()
        worldrenderer.pos(aabb.minX, aabb.maxY, aabb.maxZ).endVertex()
        worldrenderer.pos(aabb.minX, aabb.maxY, aabb.minZ).endVertex()
        worldrenderer.pos(aabb.minX, aabb.minY, aabb.minZ).endVertex()
        tessellator.draw()
        worldrenderer.begin(7, DefaultVertexFormats.POSITION)
        worldrenderer.pos(aabb.maxX, aabb.minY, aabb.minZ).endVertex()
        worldrenderer.pos(aabb.maxX, aabb.maxY, aabb.minZ).endVertex()
        worldrenderer.pos(aabb.maxX, aabb.maxY, aabb.maxZ).endVertex()
        worldrenderer.pos(aabb.maxX, aabb.minY, aabb.maxZ).endVertex()
        tessellator.draw()
        GlStateManager.color(r, g, b, a * opacity)
        worldrenderer.begin(7, DefaultVertexFormats.POSITION)
        worldrenderer.pos(aabb.minX, aabb.maxY, aabb.minZ).endVertex()
        worldrenderer.pos(aabb.maxX, aabb.maxY, aabb.minZ).endVertex()
        worldrenderer.pos(aabb.maxX, aabb.minY, aabb.minZ).endVertex()
        worldrenderer.pos(aabb.minX, aabb.minY, aabb.minZ).endVertex()
        tessellator.draw()
        worldrenderer.begin(7, DefaultVertexFormats.POSITION)
        worldrenderer.pos(aabb.minX, aabb.minY, aabb.maxZ).endVertex()
        worldrenderer.pos(aabb.maxX, aabb.minY, aabb.maxZ).endVertex()
        worldrenderer.pos(aabb.maxX, aabb.maxY, aabb.maxZ).endVertex()
        worldrenderer.pos(aabb.minX, aabb.maxY, aabb.maxZ).endVertex()
        tessellator.draw()
        GlStateManager.color(r, g, b, a)
        RenderGlobal.drawSelectionBoundingBox(aabb)
        GlStateManager.enableTexture2D()
        GlStateManager.enableDepth()
        GlStateManager.disableBlend()
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
    }

    fun drawRect(left: Double, top: Double, right: Double, bottom: Double, color: Int) {
        var leftModifiable = left
        var topModifiable = top
        var rightModifiable = right
        var bottomModifiable = bottom
        if (leftModifiable < rightModifiable) {
            val i = leftModifiable
            leftModifiable = rightModifiable
            rightModifiable = i
        }
        if (topModifiable < bottomModifiable) {
            val j = topModifiable
            topModifiable = bottomModifiable
            bottomModifiable = j
        }
        val f3 = (color shr 24 and 255).toFloat() / 255.0f
        val f = (color shr 16 and 255).toFloat() / 255.0f
        val f1 = (color shr 8 and 255).toFloat() / 255.0f
        val f2 = (color and 255).toFloat() / 255.0f
        GlStateManager.color(f, f1, f2, f3)
        val tessellator = Tessellator.getInstance()
        val worldRenderer = tessellator.worldRenderer
        GlStateManager.enableBlend()
        GlStateManager.disableTexture2D()
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0)
        worldRenderer.begin(7, DefaultVertexFormats.POSITION)
        worldRenderer.pos(leftModifiable, bottomModifiable, 0.0).endVertex()
        worldRenderer.pos(rightModifiable, bottomModifiable, 0.0).endVertex()
        worldRenderer.pos(rightModifiable, topModifiable, 0.0).endVertex()
        worldRenderer.pos(leftModifiable, topModifiable, 0.0).endVertex()
        tessellator.draw()
        GlStateManager.enableTexture2D()
        GlStateManager.disableBlend()
    }

    fun drawOutlinedEsp(entity: EntityLivingBase, model: ModelBase, color: Int, partialTicks: Float) {
        var visible = false
        if (Minecraft.getMinecraft().thePlayer.canEntityBeSeen(entity)){ visible = true }
        if (Config.disableVisible && visible) {return}
        val modelData = preModelDraw(entity, model, partialTicks)

        OutlineUtils.outlineEntity(
            model,
            entity,
            modelData.limbSwing,
            modelData.limbSwingAmount,
            modelData.age,
            modelData.rotationYaw,
            modelData.rotationPitch,
            0.0625f,
            partialTicks,
            color
        )

        postModelDraw()
    }

    fun drawChamsEsp(entity: EntityLivingBase, model: ModelBase, color: Int, partialTicks: Float) {
        if (Minecraft.getMinecraft().thePlayer.canEntityBeSeen(entity)){return}
        val modelData = preModelDraw(entity, model, partialTicks)
        val f3 = (color shr 24 and 255).toFloat() / 255f
        val f = (color shr 16 and 255).toFloat() / 255f
        val f1 = (color shr 8 and 255).toFloat() / 255f
        val f2 = (color and 255).toFloat() / 255f

        GlStateManager.pushMatrix()
        // polygonOffsetLine
        glEnable(10754)
        GlStateManager.doPolygonOffset(1f, 1000000f)
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f, 240f)

        GlStateManager.enableBlend()
        GlStateManager.disableTexture2D()
        GlStateManager.disableLighting()
        GlStateManager.blendFunc(770, 771)
        GlStateManager.color(f, f1, f2, f3)

        GlStateManager.disableDepth()
        GlStateManager.depthMask(false)

        model.render(
            entity,
            modelData.limbSwing,
            modelData.limbSwingAmount,
            modelData.age,
            modelData.rotationYaw,
            modelData.rotationPitch,
            0.0625f
        )
        renderLayers(
            modelData.renderer,
            entity,
            modelData.limbSwing,
            modelData.limbSwingAmount,
            partialTicks,
            modelData.age,
            modelData.rotationYaw,
            modelData.rotationPitch,
            0.0625f,
            f, f1, f2, f3
        )

        GlStateManager.enableDepth()
        GlStateManager.depthMask(true)
        GlStateManager.color(f, f1, f2, f3)

        model.render(
            entity,
            modelData.limbSwing,
            modelData.limbSwingAmount,
            modelData.age,
            modelData.rotationYaw,
            modelData.rotationPitch,
            0.0625f
        )
        renderLayers(
            modelData.renderer,
            entity,
            modelData.limbSwing,
            modelData.limbSwingAmount,
            partialTicks,
            modelData.age,
            modelData.rotationYaw,
            modelData.rotationPitch,
            0.0625f,
            f, f1, f2, f3
        )

        GlStateManager.enableTexture2D()
        GlStateManager.color(1f, 1f, 1f, 1f)
        GlStateManager.disableBlend()
        GlStateManager.enableLighting()

        GlStateManager.doPolygonOffset(1f, -1000000f)
        // polygonOffsetLine
        glDisable(10754)
        GlStateManager.popMatrix()

        postModelDraw()
    }

    private fun preModelDraw(entity: EntityLivingBase, model: ModelBase, partialTicks: Float): ModelData {
        val render = mc.renderManager.getEntityRenderObject<EntityLivingBase>(entity)
//        val renderManager = getRenderPositions(mc.renderManager)
        val renderManager = mc.renderManager as IMixinRenderManager
        val renderer = render as IMixinRendererLivingEntity

        val renderYaw = interpolateRotation(entity.prevRenderYawOffset, entity.renderYawOffset, partialTicks)
        val prevYaw = interpolateRotation(entity.prevRotationYawHead, entity.rotationYawHead, partialTicks)
        val rotationYaw = prevYaw - renderYaw
        val rotationPitch = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks
        val limbSwing = entity.limbSwing - entity.limbSwingAmount * (1f - partialTicks)
        val limbSwingAmout = entity.prevLimbSwingAmount + (entity.limbSwingAmount - entity.prevLimbSwingAmount) * partialTicks
        val age = entity.ticksExisted + partialTicks

        GlStateManager.pushMatrix()
        GlStateManager.disableCull()
        model.swingProgress = entity.getSwingProgress(partialTicks)
        model.isChild = entity.isChild
        val x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks.toDouble()
        val y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks.toDouble()
        val z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks.toDouble()
        GlStateManager.translate(x - renderManager.renderPosX, y - renderManager.renderPosY, z - renderManager.renderPosZ)
        val f = interpolateRotation(entity.prevRenderYawOffset, entity.renderYawOffset, partialTicks)
        rotateCorpse(entity, age, f, partialTicks)
        GlStateManager.enableRescaleNormal()
        GlStateManager.scale(-1f, -1f, 1f)
        GlStateManager.translate(0.0f, -1.5078125f, 0.0f)
        model.setLivingAnimations(entity, limbSwing, limbSwingAmout, partialTicks)
        model.setRotationAngles(limbSwing, limbSwingAmout, age, rotationYaw, rotationPitch, 0.0625f, entity)

        return ModelData(renderer, rotationYaw, rotationPitch, limbSwing, limbSwingAmout, age)
    }

    private fun postModelDraw() {
        GlStateManager.resetColor()
        GlStateManager.disableRescaleNormal()
        GlStateManager.enableTexture2D()
        GlStateManager.enableCull()
        GlStateManager.popMatrix()
    }

    fun renderLayers(
        renderer: IMixinRendererLivingEntity,
        entitylivingbaseIn: EntityLivingBase,
        p_177093_2_: Float,
        p_177093_3_: Float,
        partialTicks: Float,
        p_177093_5_: Float,
        p_177093_6_: Float,
        p_177093_7_: Float,
        p_177093_8_: Float,
        red: Float,
        green: Float,
        blue: Float,
        alpha: Float
    ) {
        if (entitylivingbaseIn !is EntitySkeleton) return
        for (layerrenderer in renderer.layerRenderers)
            if (layerrenderer is LayerArmorBase<*>)
                for (i in 1..4) {
                    val itemstack = entitylivingbaseIn.getCurrentArmor(i - 1)
                    if (itemstack == null || itemstack.item !is ItemArmor) continue

                    val armorModel = layerrenderer.getArmorModel(i)
//                    armorModel.setModelAttributes(renderer.mainModel)
                    armorModel.setLivingAnimations(entitylivingbaseIn, p_177093_2_, p_177093_3_, partialTicks)
//                    val layerrendererAccessor = layerrenderer as IMixinLayerArmorBase
//                    layerrendererAccessor.setModelPartVisible(armorModel, i)

                    GlStateManager.color(red, green, blue, alpha)
                    armorModel.render(entitylivingbaseIn, p_177093_2_, p_177093_3_, p_177093_5_, p_177093_6_, p_177093_7_, p_177093_8_)
                }
    }

    data class ModelData(
        val renderer: IMixinRendererLivingEntity,
        val rotationYaw: Float,
        val rotationPitch: Float,
        val limbSwing: Float,
        val limbSwingAmount: Float,
        val age: Float
    )
    @JvmStatic
    fun renderTexture(
        texture: ResourceLocation?,
        x: Int,
        y: Int,
        width: Int = 16,
        height: Int = 16,
        enableLighting: Boolean = true
    ) {
        if (enableLighting) RenderHelper.enableGUIStandardItemLighting()
        GlStateManager.enableRescaleNormal()
        GlStateManager.enableBlend()
        GlStateManager.enableDepth()
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0)
        GlStateManager.pushMatrix()
        mc.textureManager.bindTexture(texture)
        GlStateManager.enableRescaleNormal()
        GlStateManager.enableAlpha()
        GlStateManager.alphaFunc(516, 0.1f)
        GlStateManager.enableBlend()
        GlStateManager.blendFunc(770, 771)
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0f, 0f, width, height, width.toFloat(), height.toFloat())
        GlStateManager.disableAlpha()
        GlStateManager.disableRescaleNormal()
        GlStateManager.disableLighting()
        GlStateManager.popMatrix()
    }
}