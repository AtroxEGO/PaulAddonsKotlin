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

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import me.atroxego.pauladdons.commands.PaulAddonsCommand
import me.atroxego.pauladdons.config.Config
import me.atroxego.pauladdons.config.PersistentSave
import me.atroxego.pauladdons.features.autoExperiments.AutoChromanotron
import me.atroxego.pauladdons.features.autoExperiments.AutoSequencer
import me.atroxego.pauladdons.features.betterlootshare.ESP
import me.atroxego.pauladdons.features.betterlootshare.MobNotification
import me.atroxego.pauladdons.features.dungeons.*
import me.atroxego.pauladdons.features.dungeons.BetterStonk.createGhostBlock
import me.atroxego.pauladdons.features.dungeons.HelmetSwapper.helmetSwapper
import me.atroxego.pauladdons.features.dwarfenMines.MonolithESP
import me.atroxego.pauladdons.features.funnyFishing.BarnFishingTimer
import me.atroxego.pauladdons.features.funnyFishing.FishingTracker
import me.atroxego.pauladdons.features.funnyFishing.FunnyFishing
import me.atroxego.pauladdons.features.funnyFishing.FunnyFishing.toggleFishing
import me.atroxego.pauladdons.features.kuudra.ChaosmiteCounter
import me.atroxego.pauladdons.features.kuudra.Dropships
import me.atroxego.pauladdons.features.other.*
import me.atroxego.pauladdons.features.other.ArmorSwapper.armorSwapper
import me.atroxego.pauladdons.features.other.AutoDojo.dojoToggle
import me.atroxego.pauladdons.features.slayers.AutoDaed
import me.atroxego.pauladdons.features.slayers.AutoDaggers
import me.atroxego.pauladdons.features.slayers.SlayerESP
import me.atroxego.pauladdons.gui.GuiManager
import me.atroxego.pauladdons.render.DisplayNotification
import me.atroxego.pauladdons.utils.SBInfo
import me.atroxego.pauladdons.utils.UpdateManager
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.settings.KeyBinding
import net.minecraftforge.client.ClientCommandHandler
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.client.registry.ClientRegistry
import net.minecraftforge.fml.common.Loader
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.Mod.EventHandler
import net.minecraftforge.fml.common.ModMetadata
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import org.lwjgl.input.Keyboard
import java.io.File


@Mod(
    modid = PaulAddons.MODID,
    name = PaulAddons.MOD_NAME,
    version = PaulAddons.VERSION,
    useMetadata = true,
    clientSideOnly = true,
    acceptedMinecraftVersions = "[1.8.9]",
    dependencies= "before:*",
)
class PaulAddons {

    companion object {
        val mc: Minecraft = Minecraft.getMinecraft()
        var currentGui: GuiScreen? = null
        lateinit var configDirectory: File
        var keyBindings = arrayOfNulls<KeyBinding>(7)
        lateinit var config: Config
        const val MODID = "pauladdons"
        const val MOD_NAME = "Paul Addons"
        const val VERSION = "2.6"
        lateinit var metadata: ModMetadata
        const val prefix = "§5[§6PA§5]§8"
        var devMode = false
        val modDir by lazy { File(File(mc.mcDataDir, "config"), "pauladdons") }

        val IO = object : CoroutineScope {
            override val coroutineContext = Dispatchers.IO + SupervisorJob() + CoroutineName("PaulAddons IO")
        }

        @JvmField
        var jarFile: File? = null
        @JvmStatic
        lateinit var guiManager: GuiManager

        val json = Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
            serializersModule = SerializersModule {
                include(serializersModule)
            }
        }
    }

    @EventHandler
    fun preInit(event: FMLPreInitializationEvent) {
        println(Loader.instance().configDir)
        metadata = event.modMetadata
        val directory = File(event.modConfigurationDirectory, event.modMetadata.modId)
        directory.mkdirs()
        configDirectory = directory
        config = Config
        jarFile = event.sourceFile
        guiManager = GuiManager
    }

    @EventHandler
    fun onInit(event: FMLInitializationEvent) {
        ClientCommandHandler.instance.registerCommand(PaulAddonsCommand())
        UpdateManager.downloadDeleteTask()
        listOf(
            this,
            guiManager,
            SBInfo,
            UpdateManager,
            MobNotification,
            AutoHi,
            AutoDaed,
            Dropships,
            AutoP3GhostBlocks,
            M7DragonTimers,
            AutoChestCloser,
            ESP,
            SlayerESP,
            BonzoMask,
            AutoMelody,
            SplashThankYou,
            FunnyFishing,
            TerminalWaypoints,
            StarredMobESP,
            BetterStonk,
            ChaosmiteCounter,
            FishingTracker,
            Ping,
            SpiritMask,
            Jerry,
            AutoDojo,
            AutoDaggers,
            MonolithESP,
            RemoveBlindness,
            AutoChromanotron,
            AutoSequencer,
            BarnFishingTimer,
            DisplayNotification
        ).forEach(MinecraftForge.EVENT_BUS::register)
        Runtime.getRuntime().addShutdownHook(Thread {
            Config.markDirty()
            Config.writeData()
        })
        keyBindings[0] = KeyBinding("Open Gui", Keyboard.KEY_M, "PaulAddons")
        keyBindings[1] = KeyBinding("Funny Fishing", Keyboard.KEY_L, "PaulAddons")
        keyBindings[2] = KeyBinding("Ghost Block", Keyboard.KEY_G, "PaulAddons")
        keyBindings[3] = KeyBinding("Armor Swapper", Keyboard.KEY_O, "PaulAddons")
        keyBindings[4] = KeyBinding("Auto Dojo", Keyboard.KEY_U, "PaulAddons")
        keyBindings[5] = KeyBinding("Swap Helmet #1", Keyboard.KEY_I, "PaulAddons")
        keyBindings[6] = KeyBinding("Swap Helmet #2", Keyboard.KEY_P, "PaulAddons")
        for (keyBinding in keyBindings) {
            ClientRegistry.registerKeyBinding(keyBinding)
        }

    }
    @EventHandler
    fun postInit(event: FMLPostInitializationEvent){
        PersistentSave.loadData()
        Config.loadData()
    }


    @SubscribeEvent
    fun onTick(event: TickEvent.ClientTickEvent) {
        if (keyBindings[0]!!.isKeyDown) currentGui = Config.gui()
        if (keyBindings[1]!!.isPressed) toggleFishing()
        if (keyBindings[2]!!.isKeyDown) createGhostBlock()
        if (keyBindings[3]!!.isPressed) armorSwapper()
        if (keyBindings[4]!!.isPressed) dojoToggle()
        if (keyBindings[5]!!.isPressed) helmetSwapper(1)
        if (keyBindings[6]!!.isPressed) helmetSwapper(2)
        if (event.phase != TickEvent.Phase.START || currentGui == null) return
        mc.displayGuiScreen(currentGui)
        currentGui = null
    }

}