package me.atroxego.pauladdons.utils.core

import net.minecraft.client.renderer.GLAllocation
import net.minecraft.client.renderer.GlStateManager
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL14
import org.lwjgl.opengl.GLContext
import java.util.*

class GlState {

    companion object {
        val newBlend: Boolean
        val stack = ArrayDeque<GlState>()

        init {
            val context = GLContext.getCapabilities()
            newBlend = context.OpenGL14 || context.GL_EXT_blend_func_separate
        }

        fun pushState() {
            stack.addLast(GlState().also { it.pushState() })
        }

        fun popState() {
            stack.removeLast().popState()
        }
    }

    var lightingState = false
    var blendState = false
    var blendSrc = 0
    var blendDst = 0
    var blendAlphaSrc = 1
    var blendAlphaDst = 0
    var alphaState = false
    var depthState = false
    var colorState = GLAllocation.createDirectByteBuffer(64).asFloatBuffer()

    fun pushState() {
        lightingState = GL11.glIsEnabled(GL11.GL_LIGHTING)
        blendState = GL11.glIsEnabled(GL11.GL_BLEND)
        blendSrc = GL11.glGetInteger(GL11.GL_BLEND_SRC)
        blendDst = GL11.glGetInteger(GL11.GL_BLEND_DST)
        alphaState = GL11.glIsEnabled(GL11.GL_ALPHA_TEST)
        depthState = GL11.glIsEnabled(GL11.GL_DEPTH_TEST)
        if (newBlend) {
            blendSrc = GL11.glGetInteger(GL14.GL_BLEND_SRC_RGB)
            blendDst = GL11.glGetInteger(GL14.GL_BLEND_DST_RGB)
            blendAlphaSrc = GL11.glGetInteger(GL14.GL_BLEND_SRC_ALPHA)
            blendAlphaDst = GL11.glGetInteger(GL14.GL_BLEND_DST_ALPHA)
        }
        GL11.glGetFloat(GL11.GL_CURRENT_COLOR, colorState)
    }

    fun popState() {
        if (depthState) GlStateManager.enableDepth()
        else GlStateManager.disableDepth()

        if (blendState) GlStateManager.enableBlend()
        else GlStateManager.disableBlend()

        if (alphaState) GlStateManager.enableAlpha()
        else GlStateManager.disableAlpha()

        GlStateManager.tryBlendFuncSeparate(blendSrc, blendDst, blendAlphaSrc, blendAlphaDst)
        GlStateManager.color(colorState.get(0), colorState.get(1), colorState.get(2), colorState.get(3))
    }
}