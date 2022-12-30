package me.atroxego.pauladdons.gui.buttons

import me.atroxego.pauladdons.gui.GuiElement
import me.atroxego.pauladdons.render.RenderUtils
import net.minecraft.client.Minecraft
import net.minecraft.client.audio.SoundHandler
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.renderer.GlStateManager
import java.awt.Color

class LocationButton(var element: GuiElement) : GuiButton(-1, 0, 0, null) {
    var x = 0f
    var y = 0f
    var x2 = 0f
    var y2 = 0f

    init {
        x = this.element.actualX
        y = this.element.actualY * element.scale
        x2 = x + this.element.actualWidth
        y2 = y + this.element.actualHeight * element.scale
    }

    private fun refreshLocations() {
        x = element.actualX
        y = element.actualY + 2 * element.scale
        x2 = x + element.actualWidth
        y2 = y + element.actualHeight + 2 * element.scale
    }

    override fun drawButton(mc: Minecraft, mouseX: Int, mouseY: Int) {
        refreshLocations()
        hovered = mouseX >= x && mouseY >= y && mouseX < x2 && mouseY < y2 + 2 * element.scale
        val c = Color(255, 255, 255, if (hovered) 100 else 40)
        RenderUtils.drawRect(0.0, 0.0, element.width.toDouble(), (element.height + 4).toDouble(), c.rgb)
        GlStateManager.translate(0f, -2f, 0f)
        element.demoRender()
        GlStateManager.translate(0f, 0f, 0f)
        if (hovered) {
            lastHoveredElement = element
        }
    }

    override fun mousePressed(mc: Minecraft, mouseX: Int, mouseY: Int): Boolean {
        return enabled && visible && hovered
    }

    /**
     * get rid of clicking noise
     */
    override fun playPressSound(soundHandlerIn: SoundHandler) {}

    companion object {
        var lastHoveredElement: GuiElement? = null
    }
}