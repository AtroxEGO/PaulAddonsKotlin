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

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import me.atroxego.pauladdons.PaulAddons
import me.atroxego.pauladdons.config.PersistentSave
import java.io.File
import java.io.Reader
import java.io.Writer

object FishingData : PersistentSave(File("config/".plus(PaulAddons.MODID), "fishydata.json")) {
    var mobsCatched = hashMapOf<String, Int>()
    var lastTimeCatched = hashMapOf<String, Double>()
    override fun read(reader: Reader) {
        json.decodeFromString<Map<String, SeaCreatureData>>(reader.readText()).forEach { name, (catches, timeSince) ->
            mobsCatched[name] = catches
            lastTimeCatched[name] = timeSince
        }
    }

    override fun write(writer: Writer) {
        writer.write(json.encodeToString(mobsCatched.entries.associate {
            it.key to SeaCreatureData(
                it.value,
                lastTimeCatched[it.key] ?: 0.0
            )
        }))
    }

    override fun setDefault(writer: Writer) {
        writer.write("{}")
    }


    @Serializable
    private data class SeaCreatureData(val catches: Int, val timeSince: Double)
}