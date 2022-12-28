package me.atroxego.pauladdons.utils.core

import gg.essential.universal.UResolution
import org.apache.commons.lang3.builder.EqualsBuilder
import org.apache.commons.lang3.builder.HashCodeBuilder
import org.apache.commons.lang3.mutable.MutableFloat

class FloatPair(x: Float, y: Float) {
    private val x: MutableFloat = MutableFloat(x)
    private val y: MutableFloat = MutableFloat(y)

    constructor(x: Int, y: Int) : this(
        x / sr.scaledHeight.toFloat(),
        y / sr.scaledHeight.toFloat()
    )

    fun getX(): Float {
        return x.value
    }

    fun getY(): Float {
        return y.value
    }

    fun setY(y: Float) {
        this.y.setValue(y)
    }

    fun setX(x: Float) {
        this.x.setValue(x)
    }

    override fun equals(other: Any?): Boolean {
        if (other == null) {
            return false
        }
        if (other === this) {
            return true
        }
        if (other.javaClass != javaClass) {
            return false
        }
        val otherFloatPair = other as FloatPair
        return EqualsBuilder().append(getX(), otherFloatPair.getX()).append(getY(), otherFloatPair.getY()).isEquals
    }

    override fun hashCode(): Int {
        return HashCodeBuilder(83, 11).append(getX()).append(getY()).toHashCode()
    }

    override fun toString(): String {
        return getX().toString() + "|" + getY()
    }

    fun cloneCoords(): FloatPair {
        return FloatPair(getX(), getY())
    }

    companion object {
        private val sr = UResolution
    }

}