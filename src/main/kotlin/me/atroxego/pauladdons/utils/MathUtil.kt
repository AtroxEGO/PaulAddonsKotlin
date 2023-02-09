package me.atroxego.pauladdons.utils

/**
 * Taken from Wynntils under GNU Affero General Public License v3.0
 * https://github.com/Wynntils/Wynntils/blob/development/LICENSE
 * @author Wynntils
 */
object MathUtil {
    /**
     * returns par0 cast as an int, and no greater than Integer.MAX_VALUE-1024
     */
    fun fastFloor(value: Double) = (value + 1024.0).toInt() - 1024

    fun ceil(value: Float): Int {
        val i = value.toInt()
        return if (value > i.toFloat()) i + 1 else i
    }

    fun ceil(value: Double): Int {
        val i = value.toInt()
        return if (value > i.toDouble()) i + 1 else i
    }

    /**
     * Returns the greatest integer less than or equal to the float argument
     */
    fun floor(value: Float): Int {
        val i = value.toInt()
        return if (value < i.toFloat()) i - 1 else i
    }
}