package me.atroxego.pauladdons.features.funnyFishing

import PaulAddons
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
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