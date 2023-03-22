package me.atroxego.pauladdons.hooks.render

import me.atroxego.pauladdons.config.Config
import me.atroxego.pauladdons.utils.SBInfo
import me.atroxego.pauladdons.utils.Utils
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.scoreboard.Team
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

/**
 * Taken from Skytils under GNU Affero General Public License v3.0
 * Modified
 * https://github.com/Skytils/SkytilsMod/blob/1.x/LICENSE.md
 * @author Skytils
 */

class EntityLivingBaseHook(val entity: EntityLivingBase) {

    fun isChild(cir: CallbackInfoReturnable<Boolean>) {
        if (!SBInfo.onSkyblock) return
        if (entity is EntityPlayer){
            if (Config.realisticHeight){
                if (Config.realisticHeightType == 0) cir.returnValue = entity.name == "tripleB36"
                else {
                    if (entity.team != null) cir.returnValue = entity.team.nameTagVisibility != Team.EnumVisible.NEVER
                    else cir.returnValue = false
                }
            } else cir.returnValue = false
        } else cir.returnValue = false
    }
}