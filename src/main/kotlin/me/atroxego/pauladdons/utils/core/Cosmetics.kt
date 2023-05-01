package me.atroxego.pauladdons.utils.core

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import gg.essential.api.utils.Multithreading
import me.atroxego.pauladdons.PaulAddons.Companion.mc
import me.atroxego.pauladdons.utils.core.DatabaseConnection.fetchData
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.texture.DynamicTexture
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.MinecraftForge
import java.net.URL
import javax.imageio.ImageIO

object Cosmetics {

    private val customNames = hashSetOf<CustomName>()
    private val customCapes = hashMapOf<String,ResourceLocation>()
    private val regex = Regex("(?:§.)*(?<rank>\\[(?:§.)*\\d+(?:§.)*\\])? ?(?:§.)*(?<prefix>\\[\\w\\w\\w(?:§.)*(?:\\+(?:§.)*)*])? ?(?<username>\\w{3,16})(?:§.)*:*")
    val namesCache = hashMapOf<String, String>()
    private val gson = Gson()

    fun loadCustomNicks(){
        Multithreading.runAsync {
            customNames.forEach { MinecraftForge.EVENT_BUS.unregister(it) }
            customNames.clear()
            val response = fetchData("CustomNames").split("{\"documents\":")[1].dropLast(1)
            gson.fromJson(response, JsonArray::class.java)
                .toList()?.forEach {
                    it as JsonObject
                    val name = it.getAsJsonPrimitive("username").asString
                    if (it.getAsJsonPrimitive("animated").asBoolean) {
                        val nicks = mutableListOf<CustomName.Nick>()
                        val frames = it.getAsJsonArray("frames")
                        frames.sortedBy { frame ->
                            frame as JsonObject
                            frame.getAsJsonPrimitive("index").asInt
                        }.forEach { frame ->
                            frame as JsonObject
                            println(frame)
                            nicks.add(
                                CustomName.Nick(
                                    frame.getAsJsonPrimitive("nick").asString,
                                    frame.getAsJsonPrimitive("prefix").asString,
                                    frame.getAsJsonPrimitive("nextIn").asInt
                                )
                            )
                        }
                        customNames.add(CustomName(name, true, nicks))
                    } else customNames.add(
                        CustomName(
                            name, false,
                            listOf(
                                CustomName.Nick(
                                    it.asJsonObject.getAsJsonPrimitive("nick").asString,
                                    it.asJsonObject.getAsJsonPrimitive("prefix").asString,
                                    -1
                                )
                            )
                        )
                    )
                }
            namesCache.clear()
            println("Loaded custom nicks")
        }
    }

    fun loadCustomCapes(){
        Multithreading.run {
            customCapes.clear()
            val response = fetchData("CustomCapes").split("{\"documents\":")[1].dropLast(1)

            gson.fromJson(response, JsonArray::class.java)
                .toList()?.forEach {
                    it as JsonObject
                    val name = it.getAsJsonPrimitive("username").asString
                    val capeURL = it.getAsJsonPrimitive("capeURL").asString

                    try {
                        val imageUrl = URL(capeURL)
                        val image = ImageIO.read(imageUrl)
                        val texture = DynamicTexture(image)
                        val textureLocation = ResourceLocation("pauladdons", texture.glTextureId.toString())
                        Minecraft.getMinecraft().textureManager.loadTexture(textureLocation, texture)
                        customCapes[name] = textureLocation
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            println("Loaded custom capes")
        }
    }

    @JvmStatic
    fun getCustomCape(username: String?) : ResourceLocation? {
        if (mc.thePlayer == null || username == null) return null
        if (customCapes.containsKey(username)) return customCapes[username]
        return null
    }

    @JvmStatic
    fun getCustomNicks(message: String?): String? {
        if (mc.thePlayer == null || message == null) return message
        if (namesCache.containsValue(message)) return namesCache[message]

        var text = message!!
        val result = regex.findAll(text)
        var displace = 0
        for (matcher in result) {
            val username = matcher.groups["username"] ?: continue
            val name = username.value.trim()
            val nameRange = username.range
            val prefixRange = matcher.groups["prefix"]?.range

            val customName = customNames.find { it.name == name }?.getNick() ?: continue
            val newName = customName.nick.replace("&", "§")
            val newPrefix = customName.prefix.replace("&", "§")

            text = text.replaceRange(IntRange(nameRange.first + displace, nameRange.last + displace), newName)
            if (prefixRange != null) text = text.replaceRange(IntRange(prefixRange.first + displace, prefixRange.last + displace), newPrefix)

            displace += (newName.length - (nameRange.last - nameRange.first + 1)) + (if (prefixRange != null) (newPrefix.length - (prefixRange.last - prefixRange.first + 1)) else 0)
        }

        namesCache[message] = text
        return text
    }
}