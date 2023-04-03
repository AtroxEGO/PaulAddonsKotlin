package me.atroxego.pauladdons.hooks.render

import me.atroxego.pauladdons.config.Config
import me.atroxego.pauladdons.utils.SBInfo
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

class FarmingBlocksHook {

    fun shouldChangeSize(): Boolean {
        if (!SBInfo.onSkyblock) return false
        return Config.betterFarmingHitboxes
    }

}