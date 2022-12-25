import me.atroxego.pauladdons.commands.ExampleCommand
import me.atroxego.pauladdons.config.Config
import me.atroxego.pauladdons.config.PersistentData
import me.atroxego.pauladdons.features.betterlootshare.ESP.onRenderMob
import me.atroxego.pauladdons.utils.UpdateManager.checkUpdate
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.settings.KeyBinding
import net.minecraft.entity.EntityLivingBase
import net.minecraftforge.client.ClientCommandHandler
import net.minecraftforge.client.GuiIngameForge
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.client.event.RenderLivingEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.client.registry.ClientRegistry
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.Mod.EventHandler
import net.minecraftforge.fml.common.ModMetadata
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent
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

    @Mod.EventHandler
    fun preInit(event: FMLPreInitializationEvent) {
        metadata = event.modMetadata
        val directory = File(event.modConfigurationDirectory, event.modMetadata.modId)
        directory.mkdirs()
        configDirectory = directory
        persistentData = PersistentData.load()
        config = Config
        checkUpdate()
    }

    @Mod.EventHandler
    fun onInit(event: FMLInitializationEvent) {
        ClientCommandHandler.instance.registerCommand(ExampleCommand())
        listOf(
            this
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

    @EventHandler
    fun onLoad(event: FMLLoadCompleteEvent){
//        EssentialAPI.getDI()
//        logger.info("Checking for updates...")
//        EssentialAPI.getNotifications().push(
//            "SkySkipped",
//            "New Version Detected: 1.0\nClick to Download",
//            10f,
//            action = { Desktop.getDesktop().browse(URI("https://github.com/Cephetir/SkySkipped/releases")) }
//        )

    }

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
    }
}