package me.atroxego.pauladdons.gui

import me.atroxego.pauladdons.PaulAddons.Companion.mc
import gg.essential.universal.UResolution
import me.atroxego.pauladdons.utils.core.FloatPair

/**
 * Taken from Skytils under GNU Affero General Public License v3.0
 * Modified
 * https://github.com/Skytils/SkytilsMod/blob/1.x/LICENSE.md
 * @author Skytils
 */

abstract class GuiElement(var name: String, var scale: Float, var pos: FloatPair) {
    @JvmOverloads
    constructor(name: String, fp: FloatPair = FloatPair(0, 0)) : this(name, 1.0f, fp)

    abstract fun render()
    abstract fun demoRender()
    abstract val toggled: Boolean
    fun setPos(x: Int, y: Int) {
        val fX = x / sr.scaledWidth.toFloat()
        val fY = y / sr.scaledHeight.toFloat()
        setPos(fX, fY)
    }

    fun setPos(x: Float, y: Float) {
        pos = FloatPair(x, y)
    }

    val actualX: Float
        get() {
            val maxX = UResolution.scaledWidth
            return maxX * pos.getX()
        }
    val actualY: Float
        get() {
            val maxY = UResolution.scaledHeight
            return maxY * pos.getY()
        }
    abstract val height: Int
    abstract val width: Int
    val actualHeight: Float
        get() = height * scale
    val actualWidth: Float
        get() = width * scale

    companion object {
        val sr = UResolution
        val fr by lazy {
            mc.fontRendererObj
        }
    }

    init {
        pos = GuiManager.GUIPOSITIONS.getOrDefault(name, pos)
        scale = GuiManager.GUISCALES.getOrDefault(name, scale)
    }
}