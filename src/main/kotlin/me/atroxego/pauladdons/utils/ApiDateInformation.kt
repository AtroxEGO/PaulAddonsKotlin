package me.atroxego.pauladdons.utils

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import gg.essential.api.utils.Multithreading
import me.atroxego.pauladdons.config.Cache.currentDay
import me.atroxego.pauladdons.config.Cache.currentHour
import me.atroxego.pauladdons.config.Cache.currentMinute
import me.atroxego.pauladdons.config.Cache.currentTime
import me.atroxego.pauladdons.features.dwarfenMines.StarCult
import okhttp3.*
import okio.IOException
import java.lang.RuntimeException
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

object ApiDateInformation {
        var busy = false

    fun getDateInformation(){
        var dateData: JsonObject?
        Multithreading.runAsync {
            try {
                val client = OkHttpClient()
                val url = "https://api.slothpixel.me/api/skyblock/calendar"
                val request = Request.Builder()
                    .url(url)
                    .build()

                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        e.printStackTrace()
                    }

                    override fun onResponse(call: Call, response: Response) {
                        val json = response.body?.string() ?: ""
                        val parse = JsonParser()
                        val dataObject = parse.parse("[$json]") as JsonArray
                        dateData = dataObject[0] as JsonObject
                        currentDay = dateData!!.get("day").asInt
                        currentMinute = dateData!!.get("minute").asInt
                        currentTime = dateData!!.get("time").asString
                        currentHour = dateData!!.get("hour").asInt
                        if (currentTime.endsWith("pm") && currentHour != 12) currentHour = dateData!!.get("hour").asInt + 12
                        busy = false
                        StarCult.getNextCult()
                        StarCult.veryImportantBoolean = false
                    }
                })

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}