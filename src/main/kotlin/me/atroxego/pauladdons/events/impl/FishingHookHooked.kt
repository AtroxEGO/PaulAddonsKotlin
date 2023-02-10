package me.atroxego.pauladdons.events.impl

import me.atroxego.pauladdons.events.Cancellable
import me.atroxego.pauladdons.events.ICancellable
import me.atroxego.pauladdons.events.PaulAddonsEvent
import net.minecraft.entity.Entity

data class FishingHookHooked(
    val entity: Entity
) : PaulAddonsEvent(), ICancellable by Cancellable()