package me.atroxego.pauladdons.mixin;

import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiYesNoCallback;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiMainMenu.class)
public class GuiMainMenuMixin extends GuiScreen implements GuiYesNoCallback {

    @Inject(method = "initGui", at = @At("RETURN"))
    public void addSingleplayerMultiplayerButtons_ExampleMod(CallbackInfo ci) {
        // Change singleplayer button text
        buttonList.stream().filter(guib -> guib.id == 1).forEach(guiButton -> guiButton.displayString = "Paul Addons");
    }

}
