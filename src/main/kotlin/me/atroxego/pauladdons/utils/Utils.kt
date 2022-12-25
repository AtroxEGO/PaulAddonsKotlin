package me.atroxego.pauladdons.utils

import me.atroxego.pauladdons.features.betterlootshare.ESP.logger
import net.minecraft.client.Minecraft
import net.minecraft.client.entity.AbstractClientPlayer
import net.minecraft.client.model.ModelBase
import net.minecraft.client.model.ModelPlayer
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.client.renderer.entity.layers.LayerRenderer
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EnumPlayerModelParts
import net.minecraft.item.EnumAction
import net.minecraft.util.EnumChatFormatting
import net.minecraft.util.MathHelper
import net.minecraft.util.Timer
import net.minecraftforge.client.ForgeHooksClient
import net.minecraftforge.client.event.RenderLivingEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.entity.living.LivingEvent

object Utils {
    fun getLayerRenderers(renderer: EntityLivingBase): List<LayerRenderer<*>> {
        try {
            val field = EntityLivingBase::class.java.getDeclaredField("layerRenderers")
            field.isAccessible = true
            return field.get(renderer) as List<LayerRenderer<*>>
        } catch (e: NoSuchFieldException) {
        } catch (e: IllegalAccessException) {
        }
        return emptyList()
    }
    fun interpolateRotation(par1: Float, par2: Float, par3: Float): Float {
        var f: Float
        f = par2 - par1
        while (f < -180.0f) {
            f += 360.0f
        }
        while (f >= 180.0f) {
            f -= 360.0f
        }
        return par1 + par3 * f
    }

    public fun setModelVisibilities(clientPlayer: AbstractClientPlayer, modelBase: ModelBase) {
        val modelplayer: ModelPlayer = modelBase as ModelPlayer
        if (clientPlayer.isSpectator) {
            modelplayer.setInvisible(false)
            modelplayer.bipedHead.showModel = true
            modelplayer.bipedHeadwear.showModel = true
        } else {
            val itemstack = clientPlayer.inventory.getCurrentItem()
            modelplayer.setInvisible(true)
            modelplayer.bipedHeadwear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.HAT)
            modelplayer.bipedBodyWear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.JACKET)
            modelplayer.bipedLeftLegwear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.LEFT_PANTS_LEG)
            modelplayer.bipedRightLegwear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.RIGHT_PANTS_LEG)
            modelplayer.bipedLeftArmwear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.LEFT_SLEEVE)
            modelplayer.bipedRightArmwear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.RIGHT_SLEEVE)
            modelplayer.heldItemLeft = 0
            modelplayer.aimedBow = false
            modelplayer.isSneak = clientPlayer.isSneaking
            if (itemstack == null) {
                modelplayer.heldItemRight = 0
            } else {
                modelplayer.heldItemRight = 1
                if (clientPlayer.itemInUseCount > 0) {
                    val enumaction = itemstack.itemUseAction
                    if (enumaction == EnumAction.BLOCK) {
                        modelplayer.heldItemRight = 3
                    } else if (enumaction == EnumAction.BOW) {
                        modelplayer.aimedBow = true
                    }
                }
            }
        }
    }

    fun getRenderPositions(renderer: RenderManager): Triple<Double, Double, Double> {
        try {
            val renderPosXField = RenderManager::class.java.getDeclaredField("renderPosX")
            renderPosXField.isAccessible = true
            val renderPosYField = RenderManager::class.java.getDeclaredField("renderPosY")
            renderPosYField.isAccessible = true
            val renderPosZField = RenderManager::class.java.getDeclaredField("renderPosZ")
            renderPosZField.isAccessible = true
            return Triple(
                renderPosXField.getDouble(renderer),
                renderPosYField.getDouble(renderer),
                renderPosZField.getDouble(renderer)
            )
        } catch (e: NoSuchFieldException) {
            // Handle the exception
        } catch (e: IllegalAccessException) {
            // Handle the exception
        }
        return Triple(0.0, 0.0, 0.0)
    }
    fun rotateCorpse(bat: EntityLivingBase, p_77043_2_: Float, p_77043_3_: Float, partialTicks: Float) {
        GlStateManager.rotate(180.0f - p_77043_3_, 0.0f, 1.0f, 0.0f)
        if (bat.deathTime > 0) {
            var f: Float = (bat.deathTime.toFloat() + partialTicks - 1.0f) / 20.0f * 1.6f
            f = MathHelper.sqrt_float(f)
            if (f > 1.0f) {
                f = 1.0f
            }
            GlStateManager.rotate(f * 90.0f, 0.0f, 0.0f, 1.0f)
        } else {
            val s = EnumChatFormatting.getTextWithoutFormattingCodes(bat.getName())
            if (s != null && s == "Dinnerbone" || s == "Grumm" && (bat !is EntityPlayer || (bat as EntityPlayer).isWearing(
                    EnumPlayerModelParts.CAPE
                ))
            ) {
                GlStateManager.translate(0.0f, bat.height + 0.1f, 0.0f)
                GlStateManager.rotate(180.0f, 0.0f, 0.0f, 1.0f)
            }
        }
    }
    fun preRenderCallback(entitylivingbaseIn: EntityLivingBase?, partialTickTime: Float) {}
    fun getMinecraftTimer(): Any? {
        val minecraft = Minecraft.getMinecraft()
        try {
            val field = Timer::class.java.getDeclaredField("renderPartialTicks")
            field.isAccessible = true
            return field.get(Double)
        } catch (e: NoSuchFieldException) {
            // Handle the exception
        } catch (e: IllegalAccessException) {
            // Handle the exception
        }
        return null
    }
    fun getRenderPartialTicks(): Float {
        val minecraft = Minecraft.getMinecraft()
        try {
            val timerField = Minecraft::class.java.getDeclaredField("timer")
            timerField.isAccessible = true
            val timer = timerField.get(minecraft)
            val renderPartialTicksField = timer::class.java.getDeclaredField("renderPartialTicks")
            renderPartialTicksField.isAccessible = true
            return renderPartialTicksField.getFloat(timer)
        } catch (e: NoSuchFieldException) {
            // Handle the exception
        } catch (e: IllegalAccessException) {
            // Handle the exception
        }
        return 0.0f
    }
}