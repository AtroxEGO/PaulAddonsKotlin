package me.atroxego.pauladdons.config

import PaulAddons
import gg.essential.vigilance.Vigilant
import java.awt.Color
import java.io.File


object Config : Vigilant(
    File(PaulAddons.configDirectory, "config.toml"),
    "Paul Addons"
) {
    var betterLootShare = false
    var glowOnMob = false
    var glowColor : Color = Color.BLUE
    var disableVisible = false
    var starCultTimer = false
    var espSelector = 2
    var mobNotification = false
    var starCultTimerX = 10
    var starCultTimerY = 10
    var customESPMobs = "Zombie, Wither"
    var thunderNotification = false
    var jawbusNotification = false
    var gwSharkNotification = false
    var hydraNotification = false
    var grimNotification = false
    var empNotification = false
    var nutterNotification = false
    var yetiNotification = true
    var espOnNotifiedMobs = false
    var gcStarCultNofification = false

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
                    name = "Great Whit Shark Notification",
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
                    name = "Nut Cracker Notification",
                )
                switch(
                    Config::yetiNotification,
                    name = "Yeti Notification",
                )

            }
        }

        category("Star Cult") {
            switch(
                Config::starCultTimer,
                name = "Star Cult Timer",
                description = "Turns On and Off Cult Timer",
            )
            switch(
                Config::gcStarCultNofification,
                name = "Guild Chat Notification",
                description = "Sends a text in guild chat about star cult being active"
            )
            slider(
                Config::starCultTimerX,
                name = "Star Cult Timer Position X",
                min = 0,
                max = 950,
            )
            slider(
                Config::starCultTimerY,
                name = "Star Cult Timer Position Y",
                min = 0,
                max = 530
            )

        }
        addDependency(Config::mobNotification, Config::betterLootShare)
        addDependency(Config::glowColor, Config::glowOnMob)
        addDependency(Config::espSelector, Config::glowOnMob)
        addDependency(Config::disableVisible, Config::glowOnMob)
        addDependency(Config::starCultTimerX, Config::starCultTimer)
        addDependency(Config::gcStarCultNofification, Config::starCultTimer)
        addDependency(Config::starCultTimerY, Config::starCultTimer)
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

    }
}