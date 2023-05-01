/*
 * Paul Addons - Hypixel Skyblock QOL Mod
 * Copyright (C) 2023  AtroxEGO
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


package me.atroxego.pauladdons.features.funnyFishing

import me.atroxego.pauladdons.PaulAddons
import me.atroxego.pauladdons.PaulAddons.Companion.mc
import me.atroxego.pauladdons.config.Config
import me.atroxego.pauladdons.config.PersistentSave
import me.atroxego.pauladdons.gui.GuiElement
import me.atroxego.pauladdons.render.font.FontUtils.getTimeBetween
import me.atroxego.pauladdons.utils.core.FloatPair
import net.minecraft.init.Items
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent

object FishingTracker {

    init {
        FishingTrackerGUIElement()
    }
    public var seaCreatureMessages = mapOf(
        "A Squid appeared." to "Squid",
        "You caught a Sea Walker." to "Sea Walker",
        "You stumbled upon a Sea Guardian." to "Sea Guardian",
        "It looks like you've disrupted the Sea Witch's brewing session. Watch out, she's furious!" to "Sea Witch",
        "You reeled in a Sea Archer." to "Sea Archer",
        "The Rider of the Deep has emerged." to "Rider Of The Deep",
        "Huh? A Catfish!" to "Catfish",
        "Is this even a fish? It's the Carrot King!" to "Carrot King",
        "Gross! A Sea Leech!" to "Sea Leech",
        "You've discovered a Guardian Defender of the sea." to "Guardian Defender",
        "You have awoken the Deep Sea Protector, prepare for a battle!" to "Deep Sea Protector",
        "The Water Hydra has come to test your strength." to "Water Hydra",
        "The Sea Emperor arises from the depths." to "Sea Emperor",
        "Phew! It's only a Scarecrow." to "Scarecrow",
        "You hear trotting from beneath the waves, you caught a Nightmare." to "Nightmare",
        "It must be a full moon, a Werewolf appears." to "Werewolf",
        "The spirit of a long lost Phantom Fisher has come to haunt you." to "Phantom Fisher",
        "This can't be! The manifestation of death himself!" to "Grim Reaper",
        "Frozen Steve fell into the pond long ago, never to resurface...until now!" to "Frozen Steve",
        "It's a snowman! He looks harmless" to "Frosty The Snowman",
        "The Grinch stole Jerry's Gifts...get them back!" to "Grinch",
        "What is this creature!?" to "Yeti",
        "You found a forgotten Nutcracker laying beneath the ice." to "Nutcracker",
        "A Reindrake forms from the depths." to "Reindrake",
        "A tiny fin emerges from the water, you've caught a Nurse Shark." to "Nurse Shark",
        "You spot a fin as blue as the water it came from, it's a Blue Shark." to "Blue Shark",
        "A striped beast bounds from the depths, the wild Tiger Shark!" to "Tiger Shark",
        "Hide no longer, a Great White Shark has tracked your scent and thirsts for your blood!" to "Great White Shark",
        "From beneath the lava appears a Magma Slug." to "Magma Slug",
        "You hear a faint Moo from the lava... A Moogma appears." to "Moogma",
        "A small but fearsome Lava Leech emerges." to "Lava Leech",
        "You feel the heat radiating as a Pyroclastic Worm surfaces." to "Pyroclastic Worm",
        "A Lava Flame flies out from beneath the lava." to "Lava Flame",
        "A Fire Eel slithers out from the depths." to "Fire Eel",
        "Taurus and his steed emerge." to "Taurus",
        "You hear a massive rumble as Thunder emerges." to "Thunder",
        "You have angered a legendary creature... Lord Jawbus has arrived" to "Lord Jawbus",
        "A Water Worm surfaces!" to "Water Worm",
        "A Poisoned Water Worm surfaces!" to "Poisoned Water Worm",
        "A Zombie miner surfaces!" to "Zombie Miner",
        "A flaming worm surfaces from the depths!" to "Flaming Worm",
        "A Lava Blaze has surfaced from the depths!" to "Lava Blaze",
        "A Lava Pigman arose from the depths!" to "Lava Pigman",
    )
    var mobsCatched = FishingData.mobsCatched
    var lastTimeCatched = FishingData.lastTimeCatched

    class FishingTrackerGUIElement : GuiElement("Fishing Tracker", FloatPair(10, 10)) {

        override fun render() {
            if (!toggled) return
            if (mc.thePlayer.heldItem == null) return
            if (mc.thePlayer.heldItem.item != Items.fishing_rod) return
            if (Config.fishingTrackerSpooky || Config.fishingTrackerMarina || Config.fishingTrackerWinter || Config.fishingTrackerType == 1 || Config.fishingTrackerType == 0) {
                val left = this.actualX + this.actualWidth / 2 < mc.displayWidth / 4
                var xOffset = 0f
                var offset = 6f
                for (mob in getTrackerMobs().split(".")) {
                    fr.drawString(mob, 0f + textXoffset(mob, this), 0f + offset, 0xFFFFFF, true)
                    if (!mob.startsWith("-")) {
                        val valToDisplay =
                            if (mobsCatched[mob.removeRange(0, 2)] == null) 0 else mobsCatched[mob.removeRange(0, 2)]
                        val textToDisplay = if (left) ": $valToDisplay" else "$valToDisplay :"
                        xOffset = if (left) {
                            fr.getStringWidth(getTrackerMobs().split(".").maxBy { it.length }) + 8f
                        } else (this.width - fr.getStringWidth(
                            getTrackerMobs().split(".").maxBy { it.length })).toFloat() - 20f - getFuckingOffset(textToDisplay)
                        fr.drawString(
                            textToDisplay,
                            xOffset,
                            0f + offset,
                            0xFFFFFF,
                            true
                        )
                    }
                    offset += 9f
                }
                if (!Config.fishingTrackerTimeSince) return
                for (mob in getTimeSinceMobs().split(".")) {
                    fr.drawString(mob, 0f + textXoffset(mob, this), 0f + offset, 0xFFFFFF, true)
                    if (!mob.startsWith("-") && !mob.contains("Time Since")) {
                        var timeSince = if (lastTimeCatched[mob.removeRange(0, 2)] == null || lastTimeCatched[mob.removeRange(0, 2)] == 0.0) "Never" else getTimeBetween(
                            lastTimeCatched[mob.removeRange(0, 2)]!!, (System.currentTimeMillis() / 1000).toDouble()
                        )
                        if (mob.contains("Great White")) {
                            if (lastTimeCatched["Great White Shark"] == null || lastTimeCatched["Great White Shark"] == 0.0) timeSince = "Never"
                            else timeSince = getTimeBetween(lastTimeCatched["Great White Shark"]!!, System.currentTimeMillis()/1000.toDouble())
                        }
                        val textToDisplay = if (left) ": $timeSince" else "$timeSince :"
                        xOffset = if (left) {
                            fr.getStringWidth(getTimeSinceMobs().split(".").dropLast(1).maxBy { it.length }) + 14f
                        } else (this.width - fr.getStringWidth(
                            getTimeSinceMobs().split(".").maxBy { it.length })).toFloat() + 20f - getFuckingOffset(textToDisplay)
                        fr.drawString(textToDisplay, 0f + xOffset, 0f + offset, 0xFFFFFF, true)
                    }
                    offset += 9f
                }
            }

        }

        override fun demoRender() {
            if (Config.fishingTrackerSpooky || Config.fishingTrackerMarina || Config.fishingTrackerWinter || Config.fishingTrackerType == 1 || Config.fishingTrackerType == 0) {
                val left = this.actualX + this.actualWidth / 2 < mc.displayWidth / 4
                var xOffset = 0f
                var offset = 6f
                for (mob in getTrackerMobs().split(".")) {
                    fr.drawString(mob, 0f + textXoffset(mob, this), 0f + offset, 0xFFFFFF, true)
                    if (!mob.startsWith("-")) {
                        val valToDisplay =
                            if (mobsCatched[mob.removeRange(0, 2)] == null) 0 else mobsCatched[mob.removeRange(0, 2)]
                        val textToDisplay = if (left) ": $valToDisplay" else "$valToDisplay :"
                        xOffset = if (left) {
                            fr.getStringWidth(getTrackerMobs().split(".").maxBy { it.length }) + 8f
                        } else (this.width - fr.getStringWidth(
                            getTrackerMobs().split(".").maxBy { it.length })).toFloat() - 20f - getFuckingOffset(textToDisplay)
                        fr.drawString(
                            textToDisplay,
                            xOffset,
                            0f + offset,
                            0xFFFFFF,
                            true
                        )
                    }
                    offset += 9f
                }
                if (!Config.fishingTrackerTimeSince) return
                for (mob in getTimeSinceMobs().split(".")) {
                    fr.drawString(mob, 0f + textXoffset(mob, this), 0f + offset, 0xFFFFFF, true)
                    if (!mob.startsWith("-") && !mob.contains("Time Since")) {
                        var timeSince = if (lastTimeCatched[mob.removeRange(0, 2)] == null || lastTimeCatched[mob.removeRange(0, 2)] == 0.0) "Never" else getTimeBetween(
                            lastTimeCatched[mob.removeRange(0, 2)]!!, (System.currentTimeMillis() / 1000).toDouble()
                        )
                        if (mob.contains("Great White")) {
                            if (lastTimeCatched["Great White Shark"] == null || lastTimeCatched["Great White Shark"] == 0.0) timeSince = "Never"
                            else timeSince = getTimeBetween(lastTimeCatched["Great White Shark"]!!, System.currentTimeMillis()/1000.toDouble())
                        }
                        val textToDisplay = if (left) ": $timeSince" else "$timeSince :"
                        xOffset = if (left) {
                            fr.getStringWidth(getTimeSinceMobs().split(".").dropLast(1).maxBy { it.length }) + 14f
                        } else (this.width - fr.getStringWidth(
                            getTimeSinceMobs().split(".").maxBy { it.length })).toFloat() + 20f - getFuckingOffset(textToDisplay)
                        fr.drawString(textToDisplay, 0f + xOffset, 0f + offset, 0xFFFFFF, true)
                    }
                    offset += 9f
                }
            } else fr.drawString("Timer Is Empty", 25f, 12f, 0xFFFFFF, true)
        }

        override val toggled: Boolean
            get() = Config.fishingTracker
        override val height: Int
            get() = (getTrackerMobs().split(".").size + getTimeSinceMobs().split(".")
                .dropWhile { it == "" }.size) * 9 + 4
        override val width: Int
            get() = 115

        init {
            PaulAddons.guiManager.registerElement(this)
        }
    }

    fun getTrackerMobs(): String {
        var text = ""
        text =
            if (Config.fishingTrackerType == 0) "-----------------.§fSquid.§fSea Walker.§fSea Guardian.§aSea Witch.§aSea Archer.§aRider Of The Deep.§9Catfish.§9Carrot King.§9Sea Leech.§5Guardian Defender.§5Deep Sea Protector.§6Water Hydra.§6Sea Emperor."
            else if (Config.fishingTrackerType == 1) "-----------------.§9Magma Slug.§9Moogma.§9Lava Leech.§9Pyroclastic Worm.§9Lava Flame.§9Fire Eel.§9Taurus.§dThunder.§dLord Jawbus."
            else ""
        if (Config.fishingTrackerMarina) text += "-----------------.§cNurse Shark.§cBlue Shark.§cTiger Shark.§cGreat White Shark."
        if (Config.fishingTrackerSpooky) text += "-----------------.§fScarecrow.§aNightmare.§5Werewolf.§6Phantom Fisher.§6Grim Reaper."
        if (Config.fishingTrackerWinter) text += "-----------------.§fFrozen Steve.§fFrosty The Snowman.§aGrinch.§9Nutcracker.§6Yeti.§6Reindrake."
//        text = text.removeSuffix(".")
        text += "-----------------"
        return text
    }

    fun getFuckingOffset(text: String): Int {
        return when (text.length){
            4 -> 6
            5 -> 12
            6 -> 18
            7 -> 24
            8 -> 30
            9 -> 36
            10 -> 42
            else -> 0
        }
    }

    fun getTimeSinceMobs(): String {
//        if (!Config.fishingTrackerSpooky && !Config.fishingTrackerMarina && !Config.fishingTrackerWinter && (Config.fishingTrackerType == 0 || Config.fishingTrackerType == 2)) return ""
        if (!Config.fishingTrackerTimeSince) return ""
        var text = "§lTime Since."
        if (Config.fishingTrackerMarina) text += "§cGreat White."
        if (Config.fishingTrackerSpooky) text += "§6Grim Reaper."
        if (Config.fishingTrackerWinter) text += "§6Yeti.§6Reindrake."
        if (Config.fishingTrackerType == 1) text += "§dThunder.§dLord Jawbus."
        if (Config.fishingTrackerType == 0) text += "§6Sea Emperor."
//        text = text.removeSuffix(".")
        text += "-----------------"
        return text
    }

    fun textXoffset(text: String, element: GuiElement): Float {
        return if (element.actualX + element.actualWidth / 2 < mc.displayWidth / 4) {
            4f
        } else {
            val offset = element.width - GuiElement.fr.getStringWidth(text) - 4f
            offset
        }
    }

    @SubscribeEvent
    fun listenForSeaCreatureCatches(event: ClientChatReceivedEvent) {
        for (mobMessage in seaCreatureMessages) {
            if (event.message.unformattedText.startsWith(mobMessage.key, true)) {
                when (mobMessage.value) {
                    "Yeti" -> lastTimeCatched["Yeti"] = System.currentTimeMillis() / 1000.toDouble()
                    "Reindrake" -> lastTimeCatched["Reindrake"] = System.currentTimeMillis() / 1000.toDouble()
                    "Thunder" -> lastTimeCatched["Thunder"] = System.currentTimeMillis() / 1000.toDouble()
                    "Lord Jawbus" -> lastTimeCatched["Lord Jawbus"] = System.currentTimeMillis() / 1000.toDouble()
                    "Great White Shark" -> lastTimeCatched["Great White Shark"] = System.currentTimeMillis() / 1000.toDouble()
                    "Grim Reaper" -> lastTimeCatched["Grim Reaper"] = System.currentTimeMillis() / 1000.toDouble()
                    "Sea Emperor" -> lastTimeCatched["Sea Emperor"] = System.currentTimeMillis() / 1000.toDouble()
                }
                if (mobsCatched[mobMessage.value] != null) {
                    mobsCatched[mobMessage.value] = mobsCatched[mobMessage.value]!! + 1
//                    logger.info(mobMessage.value)
                } else {
                    mobsCatched[mobMessage.value] = 1
                }
            }
        }
    }

    @SubscribeEvent
    fun autoDetectFishingType(event: TickEvent.PlayerTickEvent) {
        if (!Config.fishingTracker) return
        if (!Config.fishingTrackerTypeAutoDetect) return
        if (event.player.fishEntity == null) return
        if (Config.fishingTrackerType == 2) return
        if (event.player.fishEntity.isInWater) Config.fishingTrackerType = 0
        if (event.player.fishEntity.isInLava) Config.fishingTrackerType = 1
    }

    @SubscribeEvent
    fun saveData(event: WorldEvent.Unload){
        PersistentSave.markDirty<FishingData>()
    }
}



