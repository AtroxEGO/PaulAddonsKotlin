
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import me.atroxego.pauladdons.commands.ExampleCommand
import me.atroxego.pauladdons.config.Config
import me.atroxego.pauladdons.config.PersistentData
import me.atroxego.pauladdons.features.betterlootshare.ESP.onRenderMob
import me.atroxego.pauladdons.features.betterlootshare.MobNotification
import me.atroxego.pauladdons.features.starcult.StarCult
import me.atroxego.pauladdons.gui.GuiManager
import me.atroxego.pauladdons.utils.ApiDateInformation.getDateInformation
import me.atroxego.pauladdons.utils.UpdateManager.checkUpdate
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.settings.KeyBinding
import net.minecraft.entity.EntityLivingBase
import net.minecraftforge.client.ClientCommandHandler
import net.minecraftforge.client.event.RenderLivingEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.client.registry.ClientRegistry
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.Mod.EventHandler
import net.minecraftforge.fml.common.ModMetadata
import net.minecraftforge.fml.common.event.FMLInitializationEvent
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
    acceptedMinecraftVersions = "[1.8.9]"
)
class PaulAddons {

    companion object {
        val mc: Minecraft = Minecraft.getMinecraft()
        var currentGui: GuiScreen? = null
        lateinit var configDirectory: File
        var keyBindings = arrayOfNulls<KeyBinding>(1)
        lateinit var config: Config
        lateinit var persistentData: PersistentData
        const val MODID = "pauladdons"
        const val MOD_NAME = "Paul Addons"
        const val VERSION = "0.3"
        lateinit var metadata: ModMetadata
        const val prefix = "§5§l[§9§lPaul Addons§5§l] §8"

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
        metadata = event.modMetadata
        val directory = File(event.modConfigurationDirectory, event.modMetadata.modId)
        directory.mkdirs()
        configDirectory = directory
        persistentData = PersistentData.load()
        config = Config
        guiManager = GuiManager
        checkUpdate()
        getDateInformation()
    }

    @EventHandler
    fun onInit(event: FMLInitializationEvent) {
        ClientCommandHandler.instance.registerCommand(ExampleCommand())
        listOf(
            this,
            guiManager,
            MobNotification,
            StarCult,
        ).forEach(MinecraftForge.EVENT_BUS::register)
        Runtime.getRuntime().addShutdownHook(object : Thread(){
            override fun run(){
                Config.markDirty()
                Config.writeData()
            }
        })
        keyBindings[0] = KeyBinding("Open Gui", Keyboard.KEY_M, "PaulAddons")
        for (keyBinding in keyBindings) {
            ClientRegistry.registerKeyBinding(keyBinding)
        }
    }

    @SubscribeEvent
    fun onTick(event: TickEvent.ClientTickEvent) {
        if (keyBindings[0]!!.isKeyDown) {
            currentGui = Config.gui();
        }
        if (event.phase != TickEvent.Phase.START || currentGui == null) return
        mc.displayGuiScreen(currentGui)
        currentGui = null
    }

    @SubscribeEvent
    fun onEntityRender(event: RenderLivingEvent.Pre<EntityLivingBase>){
        onRenderMob(event.entity, event.renderer.mainModel, event)
    }


}