package me.atroxego.pauladdons.events.impl
import me.atroxego.pauladdons.events.PaulAddonsEvent
import net.minecraft.entity.Entity

data class PlayerAttackEntityEvent(
    val targetEntity: Entity
) : PaulAddonsEvent()