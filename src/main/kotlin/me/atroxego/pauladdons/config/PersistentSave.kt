package me.atroxego.pauladdons.config

import PaulAddons
import kotlinx.serialization.json.Json
import me.atroxego.pauladdons.utils.Utils.ensureFile
import net.minecraft.client.Minecraft
import java.io.File
import java.io.Reader
import java.io.Writer
import kotlin.concurrent.fixedRateTimer
import kotlin.reflect.KClass

abstract class PersistentSave(protected val saveFile: File) {

    var dirty = false
    protected val mc: Minecraft = PaulAddons.mc
    protected val json: Json = PaulAddons.json
    abstract fun read(reader: Reader)

    abstract fun write(writer: Writer)

    abstract fun setDefault(writer: Writer)

    private fun readSave() {
        try {
            saveFile.ensureFile()
            saveFile.bufferedReader().use {
                read(it)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            try {
                saveFile.bufferedWriter().use {
                    setDefault(it)
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

    private fun writeSave() {
        try {
            saveFile.ensureFile()
            saveFile.writer().use { writer ->
                write(writer)
            }
            dirty = false
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    private fun init() {
        SAVES.add(this)
    }

    init {
        init()
    }

    companion object {
        val SAVES = HashSet<PersistentSave>()

        fun markDirty(clazz: KClass<out PersistentSave>) {
            val save =
                SAVES.find { it::class == clazz } ?: throw IllegalAccessException("PersistentSave not found")
            save.dirty = true
        }

        inline fun <reified T : PersistentSave> markDirty() {
            markDirty(T::class)
        }

        fun loadData() {
            SAVES.forEach { it.readSave() }
        }

        init {
            fixedRateTimer("PaulAddons-PersistentSave-Write", period = 30000L) {
                for (save in SAVES) {
                    if (save.dirty) save.writeSave()
                }
            }
            Runtime.getRuntime().addShutdownHook(Thread({
                for (save in SAVES) {
                    if (save.dirty) save.writeSave()
                }
            }, "PaulAddons-PersistentSave-Shutdown"))
        }
    }
}