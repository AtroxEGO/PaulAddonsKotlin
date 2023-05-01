package me.atroxego.pauladdons.utils.core

import gg.essential.api.utils.Multithreading
import gg.essential.universal.UChat
import me.atroxego.pauladdons.utils.HttpUtils

object DatabaseConnection {
    fun fetchData(collection: String) : String {
        Multithreading.run {
            val data = """{
        "dataSource": "PaulAddons",
        "database": "Users",
        "collection": "$collection",
        "filter": {},
        "sort": { "completedAt": 1 }
        }"""

            val url = "https://eu-central-1.aws.data.mongodb-api.com/app/data-zqdkt/endpoint/data/v1/action/find"
            val response = HttpUtils.sendPost(
                url,
                data,
                mapOf(
                    "apiKey" to "7nSTRxvFtfbn8EjepVoKabDkG4OHfcyapTz3kVPePrcS1KnOTmEhf8ol7d1Ri92t",
                    "Content-Type" to "application/json"
                )
            )
            if (response != null) {
                UChat.chat(response.replace("Â",""))
                return response.replace("Â","")
            } else UChat.chat("Null?")
            return "Request failed with response code: $response"
        }
    }
}