package me.atroxego.pauladdons.render.font

import me.atroxego.pauladdons.PaulAddons.Companion.mc
import me.atroxego.pauladdons.gui.GuiElement
import me.atroxego.pauladdons.render.RenderUtils
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import kotlin.math.floor

object FontUtils {
    public fun smartFontPlacement(position: Float, text: String, element: GuiElement): Float {
        return if (element.actualX + element.actualWidth / 2 < mc.displayWidth / 4) {
            position
        } else {
            var offset = 0f
            when (text.length) {
                4 -> offset = 6f
                5 -> offset = 12f
                6 -> offset = 18f
                14 -> offset = 66f
            }
            position - offset
        }
    }

    fun smartTexturePlacement(
        position: Float,
        element: GuiElement,
        resourceBasicLocation: String,
        resourceMirrorLocation: String
    ) {
        if (element.actualX + element.actualWidth / 2 < mc.displayWidth / 4) {
            val textureBasic = ResourceLocation(resourceBasicLocation)
            RenderUtils.renderTexture(textureBasic, position.toInt(), 0)
        } else {
            val textureMirrored = ResourceLocation(resourceMirrorLocation)
            RenderUtils.renderTexture(textureMirrored, (position + element.width/1.5).toInt(), 0)
        }
    }

    fun smartItemPlacement(
        element: GuiElement,
        item: ItemStack
    ) {
        var offset = 0
        if (element.actualX + element.actualWidth / 2 > mc.displayWidth / 4) offset = 34
        RenderUtils.renderItem(item, -1 + offset, 2)
    }

    fun getTimeBetween(timeOne: Double, timeTwo: Double): String {
        val secondsBetween = floor(timeTwo - timeOne)
        val timeFormatted: String
        val days: Int
        val hours: Int
        val minutes: Int
        val seconds: Int
        if (secondsBetween > 86400) {
            // More than 1d, display #d#h
            days = (secondsBetween / 86400).toInt()
            hours = (secondsBetween % 86400 / 3600).toInt()
            timeFormatted = days.toString() + "d" + hours + "h"
        } else if (secondsBetween > 3600) {
            // More than 1h, display #h#m
            hours = (secondsBetween / 3600).toInt()
            minutes = (secondsBetween % 3600 / 60).toInt()
            timeFormatted = hours.toString() + "h" + minutes + "m"
        } else {
            // Display #m#s
            minutes = (secondsBetween / 60).toInt()
            seconds = (secondsBetween % 60).toInt()
            timeFormatted = minutes.toString() + "m" + seconds + "s"
        }
        return timeFormatted
    }
}