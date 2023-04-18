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

package me.atroxego.pauladdons.utils

import PaulAddons
import kotlinx.coroutines.launch
import me.atroxego.pauladdons.gui.RequestUpdateGui
import me.atroxego.pauladdons.gui.UpdateGui
import me.atroxego.pauladdons.utils.core.GithubRelease
import net.minecraft.client.gui.GuiMainMenu
import net.minecraft.util.Util
import net.minecraftforge.client.event.GuiOpenEvent
import net.minecraftforge.fml.common.eventhandler.EventPriority
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import org.json.JSONTokener
import java.awt.Desktop
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLDecoder
import kotlin.concurrent.thread

object UpdateManager {

    val updateGetter = UpdateGetter()
    val updateDownloadURL: String
        get() = "https://github.com/AtroxEGO/PaulAddonsKotlin/releases/latest/download/PaulAddons-${updateGetter.updateObj!!.tagName}.jar"

    init {
        try {
            PaulAddons.IO.launch { updateGetter.run() }
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    fun getJarNameFromUrl(url: String): String {
        return url.split(Regex("/")).last()
    }

    fun scheduleCopyUpdateAtShutdown(jarName: String) {
        Runtime.getRuntime().addShutdownHook(Thread {
            try {
                println("Attempting to apply PaulAddons update.")
                val oldJar = PaulAddons.jarFile
                if (oldJar == null || !oldJar.exists() || oldJar.isDirectory) {
                    println("Old jar file not found.")
                    return@Thread
                }
                println("Copying updated jar to mods.")
                val newJar = File(File(PaulAddons.modDir, "updates"), jarName)
                println("Copying to mod folder")
                val nameNoExtension = jarName.substringBeforeLast(".")
                val newExtension = jarName.substringAfterLast(".")
                val newLocation = File(
                    oldJar.parent,
                    "${if (oldJar.name.startsWith("!")) "!" else ""}${nameNoExtension}${if (oldJar.endsWith(".temp.jar") && newExtension == oldJar.extension) ".temp.jar" else ".$newExtension"}"
                )
                newLocation.createNewFile()
                newJar.copyTo(newLocation, true)
                newJar.delete()
                if (oldJar.delete()) {
                    println("successfully deleted the files. skipping install tasks")
                    return@Thread
                } else  println("Fricckers!")
                println("Running delete task")
                val taskFile = File(File(PaulAddons.modDir, "updates"), "tasks").listFiles()?.last()
                if (taskFile == null) {
                    println("Task doesn't exist")
                    return@Thread
                }
                val runtime = Utils.getJavaRuntime()
                if (Util.getOSType() == Util.EnumOS.OSX) {
                    val sipStatus = Runtime.getRuntime().exec("csrutil status")
                    sipStatus.waitFor()
                    if (!sipStatus.inputStream.use { it.bufferedReader().readText() }
                            .contains("System Integrity Protection status: disabled.")) {
                        println("SIP is NOT disabled, opening Finder.")
                        Desktop.getDesktop().open(oldJar.parentFile)
                        return@Thread
                    }
                }
                println("Using runtime $runtime")
                Runtime.getRuntime().exec("\"$runtime\" -jar \"${taskFile.absolutePath}\" \"${oldJar.absolutePath}\"")
                println("Successfully applied PaulAddons update.")
            } catch (ex: Throwable) {
                println("Failed to apply PaulAddons Update.")
                ex.printStackTrace()
            }
        })
    }

    fun downloadDeleteTask() {
        thread(name = "PaulAddons-update-downloader-thread") {
            val version = HttpUtils.sendGet("https://raw.githubusercontent.com/AtroxEGO/PaulAddonsUpdater/master/latest", null)
            val url = "https://github.com/AtroxEGO/PaulAddonsUpdater/releases/latest/download/$version"
            val directory = File(File(PaulAddons.modDir, "updates"), "tasks")
            try {
                val st = URL(url).openConnection() as HttpURLConnection
                st.setRequestProperty(
                    "User-Agent",
                    "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2"
                )
                st.connect()
                if (st.responseCode != HttpURLConnection.HTTP_OK) {
                    println("$url returned status code ${st.responseCode}")
                    return@thread
                }
                if (!directory.exists() && !directory.mkdirs()) {
                    println("Couldn't create update file directory")
                    return@thread
                }
                val urlParts = url.split("/".toRegex()).toTypedArray()
                val fileSaved = File(directory, URLDecoder.decode(urlParts[urlParts.size - 1], "UTF-8"))
                val fis = st.inputStream
                val fos: OutputStream = FileOutputStream(fileSaved)
                val data = ByteArray(1024)
                var count: Int
                while (fis.read(data).also { count = it } != -1) {
                    fos.write(data, 0, count)
                }
                fos.flush()
                fos.close()
                fis.close()
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    fun onGuiOpen(e: GuiOpenEvent) {
        if (e.gui !is GuiMainMenu) return
        if (updateGetter.updateObj == null) return
        if (UpdateGui.complete) return
        if (PaulAddons.currentGui == null) PaulAddons.currentGui = RequestUpdateGui()
    }

    class UpdateGetter {
        @Volatile
        var updateObj: GithubRelease? = null

        fun run() {
            println("Checking Updates...")
            val response =
                "{data:${HttpUtils.sendGet("https://api.github.com/repos/AtroxEGO/PaulAddonsKotlin/releases", null)}}"
            val jsonObject = JSONTokener(response).nextValue() as org.json.JSONObject
            val jsonArray = jsonObject.getJSONArray("data").getJSONObject(0)
            val latestReleaseBody = jsonArray["body"].toString()
            val latestTag = jsonArray["tag_name"].toString()
            val uploader = jsonArray.getJSONObject("author").get("login").toString()
            val currentVersion = 2.9 // TODO: Change This Every Version Cuz Idk Why PA.Version doesnt update
            val latestVersion = latestTag.toDouble()
            println("$currentVersion < $latestVersion")
            println("${PaulAddons.VERSION.toDouble()} ${PaulAddons.VERSION}")
            if (currentVersion < latestVersion) updateObj = GithubRelease(latestTag, uploader, latestReleaseBody)
        }
    }
}