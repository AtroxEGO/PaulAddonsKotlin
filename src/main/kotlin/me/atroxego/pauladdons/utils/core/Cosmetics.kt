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
    private val regex = Regex("(?:§.)*(?<rank>\\[(?:§.)*\\d+(?:§.)*])? ?(?:§.)*(?<prefix>\\[\\w\\w\\w(?:§.)*(?:\\+(?:§.)*)*])? ?(?<username>\\w{3,16})(?:§.)*:*")
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
            val responsee = fetchData("CustomCapes").split("{\"documents\":")
            if(responsee.size < 2) return
            val response = responsee[1].dropLast(1)

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
}