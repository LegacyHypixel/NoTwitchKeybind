package io.github.racoondog.notwitchkeybind.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiControls;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.stream.IStream;
import net.minecraft.client.stream.NullStream;
import net.minecraft.util.ScreenShotHelper;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;


@Mixin(Minecraft.class)
public abstract class MinecraftClientMixin {
    @Shadow private IStream stream;
    @Shadow public GameSettings gameSettings;
    @Shadow @Final public File mcDataDir;
    @Shadow public int displayWidth;
    @Shadow public int displayHeight;
    @Shadow private Framebuffer framebufferMc;
    @Shadow public GuiIngame ingameGUI;
    @Shadow public abstract void toggleFullscreen();
    @Shadow public GuiScreen currentScreen;

    @Shadow
    public static long getSystemTime() {
        throw new AssertionError();
    }

    @Inject(method = "startGame", at = @At("HEAD"))
    private void earlyInitializeStream(CallbackInfo ci) {
        stream = new NullStream(null);
    }

    /**
     * @author Crosby
     * @reason Force {@link NullStream} and remove logged error.
     */
    @Overwrite
    private void initStream() {}

    /**
     * @author Crosby
     * @reason Don't handle stream key inputs.
     */
    @SuppressWarnings("ConstantConditions")
    @Overwrite
    public void dispatchKeypresses() {
        int i = Keyboard.getEventKey() == 0 ? Keyboard.getEventCharacter() : Keyboard.getEventKey();

        if (i == 0 || Keyboard.isRepeatEvent()) return;
        if (currentScreen instanceof GuiControls && ((GuiControls) currentScreen).time <= getSystemTime() - 20L) return;
        if (!Keyboard.getEventKeyState()) return;

        if (i == this.gameSettings.keyBindFullscreen.getKeyCode()) {
            this.toggleFullscreen();
        } else if (i == this.gameSettings.keyBindScreenshot.getKeyCode()) {
            this.ingameGUI.getChatGUI().printChatMessage(ScreenShotHelper.saveScreenshot(this.mcDataDir, this.displayWidth, this.displayHeight, this.framebufferMc));
        }
    }
}
