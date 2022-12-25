package me.atroxego.pauladdons.config

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
    var espSelector = 0
    var mobNotification = false

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
                    description = "Entity Glowing",
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
                switch(
                    Config::mobNotification,
                    name = "Display Notification",
                    description = "Displays notification n screen when mob spawns",
                    )
            }
        }

        category("Star Cult") {
            switch(
                Config::starCultTimer,
                name = "Star Cult Timer",
                description = "Turns On and Off Cult Timer",
            )

        }
        addDependency(Config::glowOnMob, Config::betterLootShare)
        addDependency(Config::glowColor, Config::glowOnMob)
        addDependency(Config::espSelector, Config::glowOnMob)
        addDependency(Config::disableVisible, Config::glowOnMob)
        addDependency(Config::mobNotification, Config::glowOnMob)
    }
}