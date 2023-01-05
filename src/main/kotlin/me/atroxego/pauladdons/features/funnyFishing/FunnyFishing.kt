package me.atroxego.pauladdons.features.funnyFishing

import PaulAddons.Companion.config
import PaulAddons.Companion.mc
import PaulAddons.Companion.prefix
import me.atroxego.pauladdons.config.Config
import net.minecraft.client.settings.KeyBinding
import net.minecraft.entity.projectile.EntityFishHook
import net.minecraft.init.Items
import net.minecraft.util.ChatComponentText
import net.minecraft.util.Vec3
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent


object FunnyFishing {
    var pendingRecast = false
    var pendingReelIn = false
    var tick = 0
    var reelInDelay = 20
    var recastDelay = 40
    var checkTick = 0
    var checkInterval = 100
    var movePlayer = false
    val keyBindingRight = mc.gameSettings.keyBindRight
    val keyBindingLeft = mc.gameSettings.keyBindLeft
    val sensivity = Config.sensivity
    var ticksSinceFish = 0
    var hookTimeSubmerged = 0

    fun setupFishing(){
        if (!Config.funnyFishing){
            val rodSlot = getFishingRod()
            if (rodSlot == -1){
                mc.thePlayer.addChatMessage(ChatComponentText("$prefix Haven't detected rod in hotbar"))
                return
            }
            mc.thePlayer.inventory.currentItem = rodSlot
        }
        KeyBinding.setKeyBindState(keyBindingLeft.keyCode, false)
        KeyBinding.setKeyBindState(keyBindingRight.keyCode, false)
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.keyCode, false)
        var status = ""
        Config.funnyFishing = !Config.funnyFishing
        status = if (Config.funnyFishing) "§aOn"
        else "§cOff"
        mc.thePlayer.addChatMessage(ChatComponentText("$prefix §dFunny Fishing: $status"))
        if(Config.funnyFishingMove) movePlayer = true
    }


        @SubscribeEvent
        fun fishingCheck(event: PlayerTickEvent) {
            if (!Config.funnyFishing) {
                movePlayer = false
                return
            }
            if (mc.thePlayer.heldItem == null){
                mc.thePlayer.addChatMessage(ChatComponentText("$prefix Detected slot change, disabling"))
                movePlayer = false
                KeyBinding.setKeyBindState(keyBindingLeft.keyCode, false)
                KeyBinding.setKeyBindState(keyBindingRight.keyCode, false)
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.keyCode, false)
                config.funnyFishing = false
                return
            }
            if (mc.thePlayer.heldItem.item != Items.fishing_rod){
                    mc.thePlayer.addChatMessage(ChatComponentText("$prefix Detected slot change, disabling"))
                    movePlayer = false
                    KeyBinding.setKeyBindState(keyBindingLeft.keyCode, false)
                    KeyBinding.setKeyBindState(keyBindingRight.keyCode, false)
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.keyCode, false)
                    config.funnyFishing = false
                    return
                }
            ticksSinceFish++
            val hook = getClosestFishingHook()
            if (checkTick > 0) checkTick--
            else{
                checkTick = checkInterval
                if (!pendingRecast && hook == null) {
                    hookTimeSubmerged = 0
                    recast()
                }
            }
            if (hook != null){
            if (hook.isInWater || hook.isInLava){
                hookTimeSubmerged++
            }
            }
            if (pendingReelIn) {
                tick++
                if (tick >= reelInDelay){
                    reelIn()
                    tick = 0
                    pendingReelIn = false
                }
                return
            }
            if (pendingRecast) {
                tick++
                if (tick >= recastDelay){
                    recast()
                    tick = 0
                    pendingRecast = false
                }
            return
            }
            if (!config.funnyFishing || mc.thePlayer.fishEntity == null) return
            if (ticksSinceFish < 50) return
            if (hook != null) {
//                mc.thePlayer.sendChatMessage("${hook.motionY}")
                if (hook.motionY > sensivity && (hook.isInLava || hook.isInWater) && hook.motionZ == 0.0 && hook.motionX == 0.0 && hookTimeSubmerged > 60 && ticksSinceFish > 50){
                    mc.thePlayer.addChatMessage(ChatComponentText("Detected Fish, Reeling In!"))
                    pendingReelIn = true
                    ticksSinceFish = 0
                    hookTimeSubmerged = 0
                }
            } else recast()
        }

    fun reelIn(){
        if (!Config.funnyFishing) return
        mc.playerController.sendUseItem(mc.thePlayer,mc.theWorld,mc.thePlayer.heldItem)
        pendingRecast = true
    }

    fun recast(){
        if (!Config.funnyFishing) return
        mc.playerController.sendUseItem(mc.thePlayer,mc.theWorld,mc.thePlayer.heldItem)
    }

    fun getFishingRod(): Int {
        for (i in 0..7) {
            val item = mc.thePlayer.inventory.mainInventory[i] ?: continue
            if (item.item == Items.fishing_rod) {
                return i
            }
        }
        return -1
    }
    var moveTicks = 0
    var left = false
    @SubscribeEvent
    fun movePlayer(event: PlayerTickEvent){
        if (movePlayer){
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.keyCode, true)
            moveTicks++
        if (moveTicks >= 70){
            left = !left
            moveTicks = -20
        }
            if (moveTicks > 0) return
        if (left){
            KeyBinding.setKeyBindState(keyBindingLeft.keyCode, true)
            KeyBinding.setKeyBindState(keyBindingRight.keyCode, false)
        }else{
            KeyBinding.setKeyBindState(keyBindingLeft.keyCode, false)
            KeyBinding.setKeyBindState(keyBindingRight.keyCode, true)
        }
        }
    }

    fun getClosestFishingHook(): EntityFishHook? {
        val world = mc.theWorld
        val player = mc.thePlayer
        val playerPos = Vec3(player.posX, player.posY, player.posZ)
        val radius = 10.0 // search radius in blocks
        val hooks = world.getEntitiesWithinAABB(EntityFishHook::class.java, player.entityBoundingBox.expand(radius, radius, radius))
        var closestHook: EntityFishHook? = null
        var closestDistance = Double.MAX_VALUE
        for (hook in hooks) {
            val hookPos = Vec3(hook.posX, hook.posY, hook.posZ)
            val distance = playerPos.distanceTo(hookPos)
            if (distance < closestDistance) {
                closestHook = hook
                closestDistance = distance
            }
        }
        return closestHook
    }
    }