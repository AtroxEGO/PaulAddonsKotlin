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
    var funnyFishingMove = false
    var sensivity : Float = 0.015F
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
    var deviceBeaconColor : Color = Color.BLUE
    var terminalBeaconColor : Color = Color.BLUE
    var leverBeaconColor : Color = Color.BLUE
    var betterStonk = false
    var betterStonkShiftOnly = false
    var starredESPType = 1
    var autoCloseChest = false
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
        }
        category("Star Cult") {
            switch(
                Config::starCultTimer,
                name = "Star Cult Timer",
                description = "Turns On and Off Cult Timer",
            )
            subcategory("Notification Options"){
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

        }
        category("Auto Hi"){
//            subcategory("Auto Hi"){
                switch(
                    Config::autoFriendHi,
                    name = "Auto Friend Hi",
                    description = "Automatically sends Hi message to selected friends when they join"
                )
                switch(
                    Config::autoGuildHi,
                    name = "Auto Guild Hi",
                    description = "Automatically sends Hi message to guild"
                )
//            }
            subcategory("Auto Friend Hi Options"){
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
            }
            subcategory("Auto Guild Hi Options"){
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
//                    hidden = true
                )
            }
        }
        category("Auto Thank You"){
            switch(
                Config::autoThankYou,
                name = "Auto Thank You",
                description = "Automatically thanks for a splash :D"
            )
            subcategory("Auto Thank You Options"){
                text(
                    Config::thankYouMessage,
                    name = "Thank You Message",
                    description = "For Splashers IGN use [IGN]"
                )
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
                name = "Barn Fishing Timer",
                description = "Displays a timer since first rod cast until item change //Desc TODO im sleepy"
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
            subcategory("Funny Fishing Options"){
            switch(
                Config::funnyFishingMove,
                name = "Funny Fishing Move",
                description = "Moves left and right if funny fishing enabled"
            )
            decimalSlider(
                Config::sensivity,
                name = "Sensivity",
                decimalPlaces = 3,
                min = 0.005f,
                max = 0.03f

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
        markDirty()
    }

    private class ConfigSorting : SortingBehavior() {
        private val categories = listOf(
            "Better Loot Share",
            "Dungeons",
            "Star Cult",
            "Auto Hi",
            "Auto Thank You",
            "Better Fishing",
            "Helmet Swapper",
            "GUI Locations"
        )
        override fun getCategoryComparator(): Comparator<in Category> =
            Comparator.comparingInt { category: Category -> categories.indexOf(category.name) }
    }

}