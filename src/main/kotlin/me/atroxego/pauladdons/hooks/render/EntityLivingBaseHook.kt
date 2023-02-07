/*
 * Skytils - Hypixel Skyblock Quality of Life Mod
 * Copyright (C) 2022 Skytils
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package me.atroxego.pauladdons.hooks.render

import me.atroxego.pauladdons.config.Config
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.scoreboard.Team
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable


class EntityLivingBaseHook(val entity: EntityLivingBase) {

    val blackListedNames = arrayListOf(
        "CryptDreadlord",
        "LostAdventurer",
        "Bonzo",
        "Scarf",
        "TheProfessor",
        "Livid",
        "Sadan",
        "DiamondGuy",
        "RedstoneWarrior",
        "ShadowAssassin",
        "KingMidas",
        "FrozenAdventurer",
        "CryptUndead",
        "CryptSoulstealer",
        "SkeletorPrime",
        "Prince",
        "Revoker",
        "Psycho",
        "Reaper",
        "Parasite",
        "Cannibal",
        "Mute",
        "Ooze",
        "Putrid",
        "Freak",
        "Leech",
        "Flamer",
        "Tear",
        "Skull",
        "Mr.Dead",
        "Vader",
        "Frost",
        "Walker",
        "WanderingSoul",
        "Undead",
        "UndeadWarrior",
        "UndeadPriest",
        "UndeadMage",
        "UndeadArcher",
        "SpiritBear",
        "Terracotta",
    )

    fun isChild(cir: CallbackInfoReturnable<Boolean>) {
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