package me.atroxego.pauladdons.gui.buttons

import gg.essential.universal.UResolution
import me.atroxego.pauladdons.gui.GuiElement
import me.atroxego.pauladdons.render.RenderUtils
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiButton
import org.lwjgl.input.Mouse
import java.awt.Color

/**
 * Taken from Skytils under GNU Affero General Public License v3.0
 * Modified
 * https://github.com/Skytils/SkytilsMod/blob/1.x/LICENSE.md
 * @author Skytils
 */

class ResizeButton(var x: Float, var y: Float, var element: GuiElement, val corner: Corner) :
    GuiButton(-1, 0, 0, null) {
    private var cornerOffsetX = 0f
    private var cornerOffsetY = 0f
    override fun drawButton(mc: Minecraft, mouseX: Int, mouseY: Int) {
        val scale = element.scale
        hovered = mouseX >= x && mouseY >= y && mouseX < x + SIZE * 2f * scale && mouseY < y + SIZE * 2f * scale
        val color = if (hovered) Color(255,255,255,100) else Color(255,255,255,70)
        RenderUtils.drawRect(0.0, 0.0, (SIZE * 2).toDouble(), (SIZE * 2).toDouble(), color.rgb)
    }

    override fun mousePressed(mc: Minecraft, mouseX: Int, mouseY: Int): Boolean {
        val sr = UResolution
        val minecraftScale = sr.scaleFactor.toFloat()
        val floatMouseX = Mouse.getX() / minecraftScale
        val floatMouseY = (mc.displayHeight - Mouse.getY()) / minecraftScale
        cornerOffsetX = floatMouseX
        cornerOffsetY = floatMouseY
        return hovered
    }

    enum class Corner {
        TOP_LEFT, TOP_RIGHT, BOTTOM_RIGHT, BOTTOM_LEFT
    }

    companion object {
        const val SIZE = 2
    }
}