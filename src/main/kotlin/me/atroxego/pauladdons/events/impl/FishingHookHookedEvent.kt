package me.atroxego.pauladdons.events.impl
import me.atroxego.pauladdons.events.PaulAddonsEvent
import net.minecraft.entity.Entity

data class FishingHookHookedEvent(
    val entity: Entity
) : PaulAddonsEvent()