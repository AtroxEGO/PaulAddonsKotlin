package me.atroxego.pauladdons.hooks.gui

/**
 * Taken from Skytils under GNU Affero General Public License v3.0
 * Modified
 * https://github.com/Skytils/SkytilsMod/blob/1.x/LICENSE.md
 * @author Skytils
 */

import me.atroxego.pauladdons.events.impl.SendChatMessageEvent
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

fun onSendChatMessage(message: String, addToChat: Boolean, ci: CallbackInfo) {
    if (SendChatMessageEvent(message, addToChat).postAndCatch()) ci.cancel()
}