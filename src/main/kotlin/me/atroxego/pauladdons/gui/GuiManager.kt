package me.atroxego.pauladdons.gui

import gg.essential.universal.UChat
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import me.atroxego.pauladdons.PaulAddons
import me.atroxego.pauladdons.config.PersistentSave
import me.atroxego.pauladdons.events.RenderHUDEvent
import me.atroxego.pauladdons.utils.core.FloatPair
import me.atroxego.pauladdons.utils.core.GlState
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager
import net.minecraftforge.client.GuiIngameForge
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.EventPriority
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import java.io.File
import java.io.Reader
import java.io.Writer

/**
 * Taken from Skytils under GNU Affero General Public License v3.0
 * Modified
 * https://github.com/Skytils/SkytilsMod/blob/1.x/LICENSE.md
 * @author Skytils
 */

object GuiManager : PersistentSave(File("config/".plus(PaulAddons.MODID), "guipositions.json")) {
    val GUIPOSITIONS = hashMapOf<String, FloatPair>()
    val GUISCALES = hashMapOf<String, Float>()
    val elements = hashMapOf<Int, GuiElement>()
    private val names = hashMapOf<String, GuiElement>()

    @JvmField
    var title: String? = null
    var subtitle: String? = null
    var titleDisplayTicks = 0
    var subtitleDisplayTicks = 0

    private var counter = 0
    fun registerElement(e: GuiElement): Boolean {
        return try {
            counter++
            elements[counter] = e
            names[e.name] = e
            true
        } catch (err: Exception) {
            err.printStackTrace()
            false
        }
    }

    fun getByID(ID: Int): GuiElement? {
        return elements[ID]
    }

    fun getByName(name: String?): GuiElement? {
        return names[name]
    }

    fun searchElements(query: String): List<GuiElement> {
        val results: MutableList<GuiElement> = ArrayList()
        for ((key, value) in names) {
            if (key.contains(query)) results.add(value)
        }
        return results
    }

    @SubscribeEvent
    fun renderPlayerInfo(event: RenderGameOverlayEvent.Post) {
        if (Minecraft.getMinecraft().ingameGUI !is GuiIngameForge) return
        if (event.type != RenderGameOverlayEvent.ElementType.HOTBAR) return
        GlState.pushState()
        MinecraftForge.EVENT_BUS.post(RenderHUDEvent(event))
        GlState.popState()
    }

    @JvmStatic
    fun createTitle(title: String?, ticks: Int) {
        mc.thePlayer.playSound("random.orb", 1f,0.5f)
        this.title = title
        titleDisplayTicks = ticks
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    fun onRenderHUD(event: RenderHUDEvent) {
        if (Minecraft.getMinecraft().currentScreen is LocationEditGui) return
        for ((_, element) in elements) {
            mc.mcProfiler.startSection(element.name)
            try {
                GlStateManager.pushMatrix()
                GlStateManager.translate(element.actualX, element.actualY, element.actualY)
                GlStateManager.scale(element.scale, element.scale, element.scale)
                element.render()
                GlStateManager.popMatrix()
            } catch (ex: Exception) {
                ex.printStackTrace()
                UChat.chat("${PaulAddons.prefix} Error while rendering ${element.name}. Please report this to AtroxEGO#1952")
            }
            mc.mcProfiler.endSection()
        }
        renderTitles(event.event.resolution)
        mc.mcProfiler.endSection()
    }

    @SubscribeEvent
    fun onTick(event: TickEvent.ClientTickEvent) {
        if (event.phase != TickEvent.Phase.START) return
        if (titleDisplayTicks > 0) {
            titleDisplayTicks--
        } else {
            titleDisplayTicks = 0
            title = null
        }
        if (subtitleDisplayTicks > 0) {
            subtitleDisplayTicks--
        } else {
            subtitleDisplayTicks = 0
            subtitle = null
        }
    }

    /**
     * Adapted from SkyblockAddons under MIT license
     * @link https://github.com/BiscuitDevelopment/SkyblockAddons/blob/master/LICENSE
     * @author BiscuitDevelopment
     */
    private fun renderTitles(scaledResolution: ScaledResolution) {
        val mc = Minecraft.getMinecraft()
        if (mc.theWorld == null || mc.thePlayer == null) {
            return
        }
        val scaledWidth = scaledResolution.scaledWidth
        val scaledHeight = scaledResolution.scaledHeight
        if (title != null) {
            val stringWidth = mc.fontRendererObj.getStringWidth(title)
            var scale = 4f // Scale is normally 4, but if its larger than the screen, scale it down...
            if (stringWidth * scale > scaledWidth * 0.9f) {
                scale = scaledWidth * 0.9f / stringWidth.toFloat()
            }
            GlStateManager.pushMatrix()
            GlStateManager.translate((scaledWidth / 2).toFloat(), (scaledHeight / 2).toFloat(), 0.0f)
            GlStateManager.enableBlend()
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0)
            GlStateManager.pushMatrix()
            GlStateManager.scale(scale, scale, scale) // TODO Check if changing this scale breaks anything...
            mc.fontRendererObj.drawString(
                title,
                (-mc.fontRendererObj.getStringWidth(title) / 2).toFloat(),
                -20.0f,
                0xFF0000,
                true
            )
            GlStateManager.popMatrix()
            GlStateManager.popMatrix()
        }
        if (subtitle != null) {
            val stringWidth = mc.fontRendererObj.getStringWidth(subtitle)
            var scale = 2f // Scale is normally 2, but if its larger than the screen, scale it down...
            if (stringWidth * scale > scaledWidth * 0.9f) {
                scale = scaledWidth * 0.9f / stringWidth.toFloat()
            }
            GlStateManager.pushMatrix()
            GlStateManager.translate((scaledWidth / 2).toFloat(), (scaledHeight / 2).toFloat(), 0.0f)
            GlStateManager.enableBlend()
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0)
            GlStateManager.pushMatrix()
            GlStateManager.scale(scale, scale, scale) // TODO Check if changing this scale breaks anything...
            mc.fontRendererObj.drawString(
                subtitle, -mc.fontRendererObj.getStringWidth(subtitle) / 2f, -23.0f,
                0xFF0000, true
            )
            GlStateManager.popMatrix()
            GlStateManager.popMatrix()
        }
    }

    override fun read(reader: Reader) {
        json.decodeFromString<Map<String, GuiElementLocation>>(reader.readText()).forEach { name, (x, y, scale) ->
            val pos = FloatPair(x, y)
            GUIPOSITIONS[name] = pos
            GUISCALES[name] = scale
            getByName(name)?.pos = pos
            getByName(name)?.scale = scale
        }
    }

    override fun write(writer: Writer) {
        names.entries.forEach { (n, e) ->
            GUIPOSITIONS[n] = e.pos
            GUISCALES[n] = e.scale
        }
        writer.write(json.encodeToString(GUIPOSITIONS.entries.associate {
            it.key to GuiElementLocation(
                it.value.getX(),
                it.value.getY(),
                GUISCALES[it.key] ?: 1f
            )
        }))
    }

    override fun setDefault(writer: Writer) {
        writer.write("{}")
    }
    @Serializable
    private data class GuiElementLocation(val x: Float, val y: Float, val scale: Float)
}