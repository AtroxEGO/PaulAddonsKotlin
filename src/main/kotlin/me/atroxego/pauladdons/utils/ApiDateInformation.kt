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
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

object ApiDateInformation {
        var busy = false

    fun getDateInformation(){
        var dateData: JsonObject? = null
        Multithreading.runAsync {
            var busy = true
            val url : URL
            val conn : HttpURLConnection
            try {
                url = URL("https://api.slothpixel.me/api/skyblock/calendar")
                conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "GET"

//                            conn.connect();
                val responseCode = conn.responseCode
                if (responseCode != 200) {
                    throw RuntimeException("HttpResponseCode: $responseCode")
                } else {
                    val informationString = StringBuilder()
                    val scanner = Scanner(url.openStream())
                    while (scanner.hasNext()) {
                        informationString.append(scanner.nextLine())
                    }
                    scanner.close()
                    val parse = JsonParser()
                    val dataObject = parse.parse("[$informationString]") as JsonArray
                    println(dataObject[0])
                    dateData = dataObject[0] as JsonObject
                    //                conn.disconnect();
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            currentDay = dateData!!.get("day").asInt
            currentMinute = dateData!!.get("minute").asInt
            currentTime = dateData!!.get("time").asString
            currentHour = dateData!!.get("hour").asInt
            if (currentTime.endsWith("pm") && currentHour != 12) currentHour = dateData!!.get("hour").asInt + 12
            busy = false
            StarCult.getNextCult()
            StarCult.veryImportantBoolean = false
        }
    }
}