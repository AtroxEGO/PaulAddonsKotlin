package me.atroxego.pauladdons.utils

import me.atroxego.pauladdons.config.Cache
import me.atroxego.pauladdons.config.Config
import me.atroxego.pauladdons.features.starcult.StarCult
import me.atroxego.pauladdons.features.starcult.StarCult.getNextCult
import me.atroxego.pauladdons.gui.buttons.LocationButton
import me.atroxego.pauladdons.utils.ApiDateInformation.getDateInformation
import me.atroxego.pauladdons.utils.core.FloatPair
import net.minecraft.client.Minecraft
import net.minecraft.client.entity.AbstractClientPlayer
import net.minecraft.client.model.ModelBase
import net.minecraft.client.model.ModelPlayer
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.client.renderer.entity.layers.LayerRenderer
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EnumPlayerModelParts
import net.minecraft.item.EnumAction
import net.minecraft.util.ChatComponentText
import net.minecraft.util.EnumChatFormatting
import net.minecraft.util.MathHelper
import net.minecraft.util.Timer
import java.io.File

object Utils {
    fun getLayerRenderers(renderer: EntityLivingBase): List<LayerRenderer<*>> {
        try {
            val field = EntityLivingBase::class.java.getDeclaredField("layerRenderers")
            field.isAccessible = true
            return field.get(renderer) as List<LayerRenderer<*>>
        } catch (e: NoSuchFieldException) {
        } catch (e: IllegalAccessException) {
        }
        return emptyList()
    }

    fun repair(){
        LocationButton(StarCult.StarCultTimerGuiElement()).element.scale = 1f
        LocationButton(StarCult.StarCultTimerGuiElement()).element.pos = FloatPair(0,0)
    }

    fun interpolateRotation(par1: Float, par2: Float, par3: Float): Float {
        var f: Float
        f = par2 - par1
        while (f < -180.0f) {
            f += 360.0f
        }
        while (f >= 180.0f) {
            f -= 360.0f
        }
        return par1 + par3 * f
    }

    public fun setModelVisibilities(clientPlayer: AbstractClientPlayer, modelBase: ModelBase) {
        val modelplayer: ModelPlayer = modelBase as ModelPlayer
        if (clientPlayer.isSpectator) {
            modelplayer.setInvisible(false)
            modelplayer.bipedHead.showModel = true
            modelplayer.bipedHeadwear.showModel = true
        } else {
            val itemstack = clientPlayer.inventory.getCurrentItem()
            modelplayer.setInvisible(true)
            modelplayer.bipedHeadwear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.HAT)
            modelplayer.bipedBodyWear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.JACKET)
            modelplayer.bipedLeftLegwear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.LEFT_PANTS_LEG)
            modelplayer.bipedRightLegwear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.RIGHT_PANTS_LEG)
            modelplayer.bipedLeftArmwear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.LEFT_SLEEVE)
            modelplayer.bipedRightArmwear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.RIGHT_SLEEVE)
            modelplayer.heldItemLeft = 0
            modelplayer.aimedBow = false
            modelplayer.isSneak = clientPlayer.isSneaking
            if (itemstack == null) {
                modelplayer.heldItemRight = 0
            } else {
                modelplayer.heldItemRight = 1
                if (clientPlayer.itemInUseCount > 0) {
                    val enumaction = itemstack.itemUseAction
                    if (enumaction == EnumAction.BLOCK) {
                        modelplayer.heldItemRight = 3
                    } else if (enumaction == EnumAction.BOW) {
                        modelplayer.aimedBow = true
                    }
                }
            }
        }
    }

    fun getRenderPositions(renderer: RenderManager): Triple<Double, Double, Double> {
        try {
            val renderPosXField = RenderManager::class.java.getDeclaredField("renderPosX")
            renderPosXField.isAccessible = true
            val renderPosYField = RenderManager::class.java.getDeclaredField("renderPosY")
            renderPosYField.isAccessible = true
            val renderPosZField = RenderManager::class.java.getDeclaredField("renderPosZ")
            renderPosZField.isAccessible = true
            return Triple(
                renderPosXField.getDouble(renderer),
                renderPosYField.getDouble(renderer),
                renderPosZField.getDouble(renderer)
            )
        } catch (e: NoSuchFieldException) {
            // Handle the exception
        } catch (e: IllegalAccessException) {
            // Handle the exception
        }
        return Triple(0.0, 0.0, 0.0)
    }
    fun rotateCorpse(bat: EntityLivingBase, p_77043_2_: Float, p_77043_3_: Float, partialTicks: Float) {
        GlStateManager.rotate(180.0f - p_77043_3_, 0.0f, 1.0f, 0.0f)
        if (bat.deathTime > 0) {
            var f: Float = (bat.deathTime.toFloat() + partialTicks - 1.0f) / 20.0f * 1.6f
            f = MathHelper.sqrt_float(f)
            if (f > 1.0f) {
                f = 1.0f
            }
            GlStateManager.rotate(f * 90.0f, 0.0f, 0.0f, 1.0f)
        } else {
            val s = EnumChatFormatting.getTextWithoutFormattingCodes(bat.getName())
            if (s != null && s == "Dinnerbone" || s == "Grumm" && (bat !is EntityPlayer || (bat as EntityPlayer).isWearing(
                    EnumPlayerModelParts.CAPE
                ))
            ) {
                GlStateManager.translate(0.0f, bat.height + 0.1f, 0.0f)
                GlStateManager.rotate(180.0f, 0.0f, 0.0f, 1.0f)
            }
        }
    }
    fun preRenderCallback(entitylivingbaseIn: EntityLivingBase?, partialTickTime: Float) {}
    fun getMinecraftTimer(): Any? {
        val minecraft = Minecraft.getMinecraft()
        try {
            val field = Timer::class.java.getDeclaredField("renderPartialTicks")
            field.isAccessible = true
            return field.get(Double)
        } catch (e: NoSuchFieldException) {
            // Handle the exception
        } catch (e: IllegalAccessException) {
            // Handle the exception
        }
        return null
    }
    fun getRenderPartialTicks(): Float {
        val minecraft = Minecraft.getMinecraft()
        try {
            val timerField = Minecraft::class.java.getDeclaredField("timer")
            timerField.isAccessible = true
            val timer = timerField.get(minecraft)
            val renderPartialTicksField = timer::class.java.getDeclaredField("renderPartialTicks")
            renderPartialTicksField.isAccessible = true
            return renderPartialTicksField.getFloat(timer)
        } catch (e: NoSuchFieldException) {
            // Handle the exception
        } catch (e: IllegalAccessException) {
            // Handle the exception
        }
        return 0.0f
    }
    fun sendItemTags(){
        val player = Minecraft.getMinecraft().thePlayer
        val itemStack = player.heldItem
        val nbtTags = itemStack.tagCompound // Get the NBT tags for the item
        val nbtTagsString = nbtTags.toString() // Get a string representation of the NBT tags
        Minecraft.getMinecraft().thePlayer.addChatMessage(ChatComponentText(nbtTagsString))
        }
    fun itemHasTags(){
        val player = Minecraft.getMinecraft().thePlayer
        val itemStack = player.heldItem
        Minecraft.getMinecraft().thePlayer.addChatMessage(ChatComponentText(itemStack.hasTagCompound().toString()))
    }
    private val STRIP_COLOR_PATTERN = Regex("(?i)§[\\dA-FK-OR]")

    fun dateStuff(){
        getDateInformation()
        getNextCult()
        Minecraft.getMinecraft().thePlayer.addChatMessage(ChatComponentText("Day: " + Cache.currentDay +" Hour: " +Cache.currentHour+ " Minute: " + Cache.currentMinute))
    }

    fun dateSet(day: String, hour: String, minute: String){
        Cache.currentDay = day.toInt()
        Cache.currentHour = hour.toInt()
        Cache.currentMinute = minute.toInt()
        getNextCult()
    }
    var lastTimeChecked : Long = 0
    var customMobs = ""
    fun getCustomMobsReal(): String {
        if (System.currentTimeMillis() - lastTimeChecked > 5000){
            customMobs = Config.customESPMobs
            lastTimeChecked = System.currentTimeMillis()
            return customMobs
        } else return customMobs


    }

    @JvmStatic
    fun String.stripColor(): String = STRIP_COLOR_PATTERN.replace(this, "")

    fun File.ensureFile() = (parentFile.exists() || parentFile.mkdirs()) && createNewFile()

    fun getMobsForNotification(): HashMap<String, String> {
//        var mobsSelected = arrayListOf<String>()
//        if (Config.empNotification) mobsSelected.add("Sea Emperior")
//        else{
//            mobsSelected.remove("Sea Emperior")
//        }
//        if (Config.thunderNotification) mobsSelected.add("Thunder")
//        if (Config.grimNotification) mobsSelected.add("Grim Reaper")
//        if (Config.gwSharkNotification) mobsSelected.add("Great White Shark")
//        if (Config.hydraNotification) mobsSelected.add("Hydra")
//        if (Config.jawbusNotification) mobsSelected.add("Lord Jawbus")
//        if (Config.nutterNotification) mobsSelected.add("Nut Cracker")
//        if (Config.yetiNotification) mobsSelected.add("Yeti")

        var mobsSelected = hashMapOf<String, String>()
        if (Config.nutterNotification) mobsSelected["Nutcracker"] = "§9"
        if (Config.gwSharkNotification) mobsSelected["Great White Shark"] = "§c"
        if (Config.thunderNotification) mobsSelected["Thunder"] = "§d"
        if (Config.jawbusNotification) mobsSelected["Lord Jawbus"] = "§d"
        if (Config.grimNotification) mobsSelected["Grim Reaper"] = "§6"
        if (Config.yetiNotification) mobsSelected["Yeti"] = "§6"
        if (Config.hydraNotification) mobsSelected["Hydra"] = "§6"
        return mobsSelected
    }

    }


