package me.atroxego.pauladdons.hooks.render

import me.atroxego.pauladdons.events.impl.FishingHookHookedEvent
import net.minecraft.entity.Entity

class FishEntityHooked {

    companion object {
        @JvmStatic
        fun fishEntityHooked(entity: Entity) {
            FishingHookHookedEvent(entity).postAndCatch()
        }
    }
}