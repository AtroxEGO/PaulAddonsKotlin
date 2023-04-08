package me.atroxego.pauladdons.hooks.render

import me.atroxego.pauladdons.config.Config
import me.atroxego.pauladdons.utils.SBInfo

class FarmingBlocksHook {

    fun shouldChangeSize(): Boolean {
        if (!SBInfo.onSkyblock) return false
        return Config.betterFarmingHitboxes
    }

}