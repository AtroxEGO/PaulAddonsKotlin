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


package me.atroxego.pauladdons.config

import PaulAddons
import gg.essential.vigilance.Vigilant
import gg.essential.vigilance.data.Category
import gg.essential.vigilance.data.SortingBehavior
import me.atroxego.pauladdons.gui.LocationEditGui
import java.awt.Color
import java.io.File


object Config : Vigilant(
    File(PaulAddons.configDirectory, "config.toml"),
    "Paul Addons",
    sortingBehavior = ConfigSorting()
) {
    var betterLootShare = false
    var glowOnMob = false
    var glowColor : Color = Color.BLUE
    var disableVisible = false
    var starCultTimer = false
    var espSelector = 2
    var mobNotification = false
    var customESPMobs = "MobOne, MobTwo"
    var thunderNotification = false
    var jawbusNotification = false
    var gwSharkNotification = false
    var hydraNotification = false
    var grimNotification = false
    var empNotification = false
    var nutterNotification = false
    var yetiNotification = false
    var espOnNotifiedMobs = false
    var gcStarCultNofification = false
    var cStarCultNotification = false
    var screenStarCultNotification = false
    var autoFriendHi = false
    var autoHiFriends = "PlayerOne, PlayerTwo"
    var autoFriendHiCooldown = 3
    var autoFriendHiType = 0
    var autoHiCustomCommand = "/msg [IGN] Hi [IGN]!"
    var autoGuildHi = false
    var lastGuildHi = 0
    var autoGuildHiCustomMessage = "Hi Guild!"
    var autoGuildHiFrequency = 0
    var autoThankYou = false
    var thankYouMessage = "Thank you [IGN]! <3"
    var starCultGuildMessage = "[Paul Addons] Star Cult Wee Woo"
    var funnyFishing = false
    var barnFishingTimer = false
    var displayBarnFishingTimerNotification = false
    var timestampOfBarnFishingNotification = 240
    var barnFishingTimerText = "Kill"
    var fishingTracker = false
    var fishingTrackerType = 0
    var fishingTrackerTypeAutoDetect = false
    var fishingTrackerMarina = false
    var fishingTrackerSpooky = false
    var fishingTrackerWinter = false
    var fishingTrackerTimeSince = false
    var helmetToSwapNameOne = "Rabbit Hat"
    var helmetToSwapNameTwo = "Bonzo Mask"
    var starredMobESP = false
    var starredMobESPColor : Color = Color.BLUE
    var terminalWaypoints = false
    var monolithESP = false
    var deviceBeaconColor : Color = Color.BLUE
    var terminalBeaconColor : Color = Color.BLUE
    var leverBeaconColor : Color = Color.BLUE
    var betterStonk = false
    var betterStonkShiftOnly = false
    var starredESPType = 1
    var autoCloseChest = false
    var hideDefaultNames = false
    var removeBlindness = false
    var autoExperiments = false
    var autoCloseExperiments = false
    var autoExperimentsDelay = 200
    var miniESP = false
    var slayerESP = false
    var bossESPColor : Color = Color.BLUE
    var worseMiniColor : Color = Color.BLUE
    var betterMiniColor : Color = Color.BLUE
    var slayerESPType = 2
    var bossESP = false
    var autoDaed = false
    var daedSwapHealthType = 0
    var percentageHealthDaed = 0f
    var manualHealthDaed = "150k"
    var autoP3P5GhostBlocks = false
    var bonzoMaskTimer = false
    var autoMelody = false
    var spiritMaskTimer = false
    var autoBonzoMask = false
    var autoBonzoMaskHealth = 0.3f
    var realisticHeight = true
    var realisticHeightType = 0
    var jerryKB = false
    var autoBlazeDaggers = false
    var fishingMove = false
    var fishingRotate = false
    var fishingKilling = false
    var fishingTotem = false
    var fishingRecastDelay = 275
    var dropshipNotification = false
    init {
        category("Better Loot Share") {
            subcategory("Better Loot Share") {
                switch(
                    Config::betterLootShare,
                    name = "Better Loot Share"
                )
            }
            subcategory("ESP Options") {
                switch(
                    Config::glowOnMob,
                    name = "ESP",
                    description = "Entity Glowing On Mobs",
                )
                switch(
                    Config::espOnNotifiedMobs,
                    name = "ESP On Notified Mobs",
                    description = "Renders ESP on mobs that were selected in Mob Notification subcategory"
                )
                text(
                    Config::customESPMobs,
                    name = "Custom ESP Mobs",
                    description = "Type names of mobs separated by ', '"
                )
                selector(
                    Config::espSelector,
                    name = "Type Of ESP",
                    description = "Changes the type of ESP",
                    options = listOf("Chams", "Box", "Outline"),
                )
                color(
                    Config::glowColor,
                    name = "Glow Color",
                )
                switch(
                    Config::disableVisible,
                    name = "Disable on Visible",
                    description = "Disables ESP Rendering On Mobs Visible By Player",

                    )
            }
            subcategory("Mob Notification"){
                switch(
                    Config::mobNotification,
                    name = "Display Notification",
                    description = "Displays notification on the screen when mob spawns",
                )
                switch(
                    Config::thunderNotification,
                    name = "Thunder Notification",
                )
                switch(
                    Config::jawbusNotification,
                    name = "Jawbus Notification",
                )
                switch(
                    Config::gwSharkNotification,
                    name = "Great White Shark Notification",
                )
                switch(
                    Config::hydraNotification,
                    name = "Hydra Notification",
                )
                switch(
                    Config::grimNotification,
                    name = "Grim Reaper Notification",
                )
                switch(
                    Config::empNotification,
                    name = "Sea Emperior Notification",
                )
                switch(
                    Config::nutterNotification,
                    name = "Nutcracker Notification",
                )
                switch(
                    Config::yetiNotification,
                    name = "Yeti Notification",
                )

            }
        }
        category("Dungeons"){
            switch(
                Config::starredMobESP,
                name = "Starred Mob ESP"
            )
            color(
                Config::starredMobESPColor,
                name = "Starred Mob ESP Color"
            )
            selector(
                Config::starredESPType,
                name = "Type of Starred ESP",
                description = "Changes the type of ESP",
                options = listOf("Chams", "Box", "Outline"),
            )
            subcategory("Secrets"){
                switch(
                    Config::autoCloseChest,
                    name = "Auto Close Secret Chests"
                )
            }
            subcategory("Auto Bonzo"){
                switch(
                    Config::autoBonzoMask,
                    name = "Auto Bonzo Mask"
                )
                percentSlider(
                    Config::autoBonzoMaskHealth,
                    name = "Percentage of health to swap at"
                )
            }
            subcategory("Terminal Waypoints"){
            switch(
                Config::terminalWaypoints,
                name = "Terminal Waypoints",
                description = "Creates a waypoint at unfinished terminal location"
            )
            color(
                Config::deviceBeaconColor,
                name = "Device Beacon Color"
            )
            color(
                Config::leverBeaconColor,
                name = "Lever Beacon Color"
            )
            color(
                Config::terminalBeaconColor,
                name = "Terminal Beacon Color"
            )
                switch(
                    Config::hideDefaultNames,
                    name = "Hide Default Text",
                    description = "Hides default text next to terminals"
                )
            }
            subcategory("Better Stonk"){
            switch(
                Config::betterStonk,
                name = "Better Stonk"
            )
            switch(
                Config::betterStonkShiftOnly,
                name = "Shift Only"
            )
            }
            subcategory("Other"){
                switch(
                    Config::removeBlindness,
                    name = "Remove Blindness"
                )
                switch(
                    Config::jerryKB,
                    name = "Jerry Knockback",
                    description = "Only recive vertical knockback"
                )
                switch(
                    Config::bonzoMaskTimer,
                    name = "Bonzo Mask Timer"
                )
                switch(
                    Config::spiritMaskTimer,
                    name = "Spirit Mask Timer"
                )
            switch(
                Config::autoP3P5GhostBlocks,
                name = "Auto P3/P5 Ghost Blocks"
            )
            }
        }
        category("Dwarven Mines") {
            subcategory("Star Cult"){
            switch(
                Config::starCultTimer,
                name = "Star Cult Timer",
                description = "Turns On and Off Cult Timer",
            )
                switch(
                    Config::cStarCultNotification,
                    name = "Chat Notification",
                    description = "Displays a message in chat about star cult being active"
                )
                switch(
                    Config::gcStarCultNofification,
                    name = "Guild Chat Notification",
                    description = "Sends a text in guild chat about star cult being active"
                )
                text(
                    Config::starCultGuildMessage,
                    name = "Star Cult Guild Message"
                )
                switch(
                    Config::screenStarCultNotification,
                    name = "Screen Notification",
                    description = "Displays notification on the screen about star cult being active"
                )
            }
            subcategory("Monoliths"){
                switch(
                    Config::monolithESP,
                    name = "Monolith ESP"
                )
            }
        }
        category("Auto Stuff") {
            subcategory("Auto Melody"){
                switch(
                    Config::autoMelody,
                    name = "Auto Melody"
                )
            }
            subcategory("Auto Hi") {
                switch(
                    Config::autoFriendHi,
                    name = "Auto Friend Hi",
                    description = "Automatically sends Hi message to selected friends when they join"
                )
                text(
                    Config::autoHiFriends,
                    name = "Auto Hi Friends",
                    description = "Type IGN's of friends separated by ', '"
                )
                slider(
                    Config::autoFriendHiCooldown,
                    name = "Auto Hi Cooldown",
                    description = "Cooldown between messages per player in seconds",
                    min = 0,
                    max = 600,
                )
                selector(
                    Config::autoFriendHiType,
                    name = "Message Type",
                    description = "Change Between MSG and Boop",
                    options = listOf("Msg", "Boop")
                )
                text(
                    Config::autoHiCustomCommand,
                    name = "Custom Command",
                    description = "Type custom command, for ign use [IGN], leave empty for default"
                )
                switch(
                    Config::autoGuildHi,
                    name = "Auto Guild Hi",
                    description = "Automatically sends Hi message to guild"
                )
                text(
                    Config::autoGuildHiCustomMessage,
                    name = "Custom Guild Hi Message",
                )
                selector(
                    Config::autoGuildHiFrequency,
                    name = "Auto Guild Hi Frequency",
                    options = listOf("Once Per Day", "Every Skyblock Join")
                )
                slider(
                    Config::lastGuildHi,
                    name = "Last Guild Hi",
                    min = 0,
                    max = 31,
                    hidden = true
                )
            }
            subcategory("Auto Thank You") {
                switch(
                    Config::autoThankYou,
                    name = "Auto Thank You",
                    description = "Automatically thanks for a splash :D"
                )
                subcategory("Auto Thank You Options") {
                    text(
                        Config::thankYouMessage,
                        name = "Thank You Message",
                        description = "For Splashers IGN use [IGN]"
                    )
                }
            }
        }
        category("Better Fishing"){
            switch(
                Config::fishingTracker,
                name = "Fishing Tracker",
                description = "Tracks your Sea Creature catches"
            )
            selector(
                Config::fishingTrackerType,
                name = "Fishing Tracker Type",
                options = listOf("Water Fishing", "Lava Fishing", "None")
            )
            switch(
                Config::fishingTrackerTypeAutoDetect,
                name = "Fishing Tracker Type Auto Detect",
                description = "Auto detects type of fishing\nNote: Wont work if Tracker Type is None!"
            )
            switch(
                Config::fishingTrackerTimeSince,
                name = "Display Time Since",
                description = "Displays time since rare sea creatures"
            )
            checkbox(
                Config::fishingTrackerMarina,
                name = "Display Marina Mobs",
            )
            checkbox(
                Config::fishingTrackerSpooky,
                name = "Display Spooky Mobs",
            )
            checkbox(
                Config::fishingTrackerWinter,
                name = "Display Winter Mobs",
            )
            subcategory("Fishing Timer"){
            switch(
                Config::barnFishingTimer,
                name = "Fishing Timer"
            )
            switch(
                Config::displayBarnFishingTimerNotification,
                name = "Barn Fishing Timer Notification",
                description = "Display Notification when timer gets to selected time"
            )
            text(
                Config::barnFishingTimerText,
                name = "Notification Text"
            )
            slider(
                Config::timestampOfBarnFishingNotification,
                name = "Timestamp of Notification",
                description = "When to display a notification in seconds",
                min = 10,
                max = 300
            )
            }
            subcategory("Auto Fishing"){
                switch(
                    Config::fishingRotate,
                    name = "Fishing Rotations"
                )
                switch(
                    Config::fishingMove,
                    name = "Fishing Movement"
                )
                switch(
                    Config::fishingKilling,
                    name = "Auto Killing",
                    description = "Needs Fire Veil Wand"
                )
                switch(
                    Config::fishingTotem,
                    name = "Auto Totem"
                )
                slider(
                    Config::fishingRecastDelay,
                    name = "Recast Delay",
                    min = 200,
                    max = 500
                )
            }
        }
        category("Helmet Swapper"){
            text(
                Config::helmetToSwapNameOne,
                name = "Helmet #1 Name",
            )
            text(
                Config::helmetToSwapNameTwo,
                name = "Helmet #2 Name",
            )
        }
        category("Auto Experiments"){
            switch(
                Config::autoExperiments,
                name = "Auto Experiments"
            )
            switch(
                Config::autoCloseExperiments,
                name = "Auto Close Experiments",
                description = "Automatically closes the window after getting 3 clicks"
            )
            slider(
                Config::autoExperimentsDelay,
                name = "Click delay in ms",
                min = 50,
                max = 1000,
                description = "Too low will casue macro to not click a block at the end"
            )
        }
        category("Slayer"){
            subcategory("Slayer ESP"){
                switch(
                    Config::slayerESP,
                    name = "Slayer ESP"
                )
                selector(
                    Config::slayerESPType,
                    name = "Type of Slayer ESP",
                    description = "Changes the type of ESP",
                    options = listOf("Chams", "Box", "Outline")
                )
                switch(
                    Config::bossESP,
                    name = "Boss ESP"
                )
                switch(
                    Config::miniESP,
                    name = "Mini ESP"
                )
                color(
                    Config::bossESPColor,
                    name = "Boss Color"
                )
                color(
                    Config::worseMiniColor,
                    name = "Weak Mini Color"
                )
                color(
                    Config::betterMiniColor,
                    name = "Strong Mini Color"
                )
            }
            subcategory("Auto Daed Swap"){
                switch(
                    Config::autoDaed,
                    name = "Auto Daed Swap"
                )
                selector(
                    Config::daedSwapHealthType,
                    name = "Health Detection Type",
                    options = listOf("Percentage", "Manual")
                )
                percentSlider(
                    Config::percentageHealthDaed,
                    name = "Percentage Boss Health",
                    description = "Select at what percentage of health swap to daed",
                )
                text(
                    Config::manualHealthDaed,
                    name = "Manual Boss Health",
                    description = "Select at what amount of health swap to daed, for example: '10.2M'"
                )
            }
            switch(
                Config::autoBlazeDaggers,
                name = "Auto Blaze Daggers (Not Avaiable For Now)"
            )
        }
        category("Miscellaneous"){
            switch(
                Config::dropshipNotification,
                name = "Dropship Notification"
            )
            switch(
                Config::realisticHeight,
                name = "Realistic Height"
            )
            selector(
                Config::realisticHeightType,
                name = "Realistic Height Type",
                options = listOf("Only tripleB36", "Everyone"),
            )
        }
        category("GUI Locations"){
            button(
                name = "Edit GUI Locations",
                description = "Also /pa gui",
                buttonText = "Edit",
            ) {
                PaulAddons.currentGui = LocationEditGui()
            }
        }
        addDependency(Config::starCultGuildMessage, Config::starCultTimer)
        addDependency(Config::thankYouMessage,Config::autoThankYou)
        addDependency(Config::starredMobESPColor,Config::starredMobESP)
        addDependency(Config::starredESPType ,Config::starredMobESP )
        addDependency(Config::mobNotification, Config::betterLootShare)
        addDependency(Config::glowColor, Config::glowOnMob)
        addDependency(Config::espSelector, Config::glowOnMob)
        addDependency(Config::disableVisible, Config::glowOnMob)
        addDependency(Config::gcStarCultNofification, Config::starCultTimer)
        addDependency(Config::cStarCultNotification, Config::starCultTimer)
        addDependency(Config::screenStarCultNotification, Config::starCultTimer)
        addDependency(Config::customESPMobs, Config::glowOnMob)
        addDependency(Config::glowOnMob, Config::betterLootShare)
        addDependency(Config::espOnNotifiedMobs, Config::glowOnMob)
        addDependency(Config::thunderNotification, Config::mobNotification)
        addDependency(Config::thunderNotification, Config::mobNotification)
        addDependency(Config::jawbusNotification, Config::mobNotification)
        addDependency(Config::gwSharkNotification, Config::mobNotification)
        addDependency(Config::hydraNotification, Config::mobNotification)
        addDependency(Config::grimNotification, Config::mobNotification)
        addDependency(Config::empNotification , Config::mobNotification)
        addDependency(Config::yetiNotification, Config::mobNotification)
        addDependency(Config::nutterNotification, Config::mobNotification)
        addDependency(Config::autoHiFriends, Config::autoFriendHi)
        addDependency(Config::autoFriendHiType, Config::autoFriendHi)
        addDependency(Config::autoHiCustomCommand, Config::autoFriendHi)
        addDependency(Config::autoFriendHiCooldown, Config::autoFriendHi)
        addDependency(Config::autoGuildHiCustomMessage, Config::autoGuildHi)
        addDependency(Config::autoGuildHiFrequency, Config::autoGuildHi)
        addDependency(Config::thankYouMessage, Config::autoThankYou)
        addDependency(Config::displayBarnFishingTimerNotification, Config::barnFishingTimer)
        addDependency(Config::timestampOfBarnFishingNotification, Config::barnFishingTimer)
        addDependency(Config::barnFishingTimerText, Config::barnFishingTimer)
        addDependency(Config::fishingTrackerTimeSince, Config::fishingTracker)
        addDependency(Config::fishingTrackerType, Config::fishingTracker)
        addDependency(Config::fishingTrackerTypeAutoDetect, Config::fishingTracker)
        addDependency(Config::fishingTrackerMarina, Config::fishingTracker)
        addDependency(Config::fishingTrackerSpooky, Config::fishingTracker)
        addDependency(Config::fishingTrackerWinter, Config::fishingTracker)
        addDependency(Config::betterStonkShiftOnly ,Config::betterStonk)
        addDependency(Config::deviceBeaconColor ,Config::terminalWaypoints)
        addDependency(Config::leverBeaconColor ,Config::terminalWaypoints)
        addDependency(Config::terminalBeaconColor ,Config::terminalWaypoints)
        addDependency(Config::hideDefaultNames, Config::terminalWaypoints)
        addDependency(Config::autoCloseExperiments, Config::autoExperiments)
        addDependency(Config::autoExperimentsDelay, Config::autoExperiments)
        addDependency(Config::slayerESPType, Config::slayerESP)
        addDependency(Config::bossESP, Config::slayerESP)
        addDependency(Config::miniESP, Config::slayerESP)
        addDependency(Config::bossESPColor, Config::bossESP)
        addDependency(Config::betterMiniColor, Config::miniESP)
        addDependency(Config::worseMiniColor, Config::miniESP)
        addDependency(Config::daedSwapHealthType, Config::autoDaed)
        addDependency(Config::percentageHealthDaed, Config::autoDaed)
        addDependency(Config::manualHealthDaed, Config::autoDaed)
        addDependency(Config::autoBonzoMaskHealth, Config::autoBonzoMask)
        addDependency(Config::realisticHeightType, Config::realisticHeight)
        markDirty()
    }

    private class ConfigSorting : SortingBehavior() {
        private val categories = listOf(
            "Better Loot Share",
            "Dungeons",
            "Dwarven Mines",
            "Auto Stuff",
            "Auto Thank You",
            "Better Fishing",
            "Helmet Swapper",
            "Auto Experiments",
            "Slayer",
            "Miscellaneous",
            "GUI Locations"
        )
        override fun getCategoryComparator(): Comparator<in Category> =
            Comparator.comparingInt { category: Category -> categories.indexOf(category.name) }
    }

}