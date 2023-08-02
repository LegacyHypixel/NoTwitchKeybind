package io.github.racoondog.notwitchkeybind.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.options.ControlsOptionsScreen;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.util.NullTwitchStream;
import net.minecraft.client.util.ScreenshotUtils;
import net.minecraft.client.util.TwitchStreamProvider;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;

@Environment(EnvType.CLIENT)
@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
    @Shadow private TwitchStreamProvider twitchStreamProvider;
    @Shadow public GameOptions options;
    @Shadow @Final public File runDirectory;
    @Shadow public int width;
    @Shadow public int height;
    @Shadow private Framebuffer fbo;
    @Shadow public InGameHud inGameHud;
    @Shadow public abstract void toggleFullscreen();
    @Shadow public Screen currentScreen;

    @Shadow
    public static long getTime() {
        throw new AssertionError();
    }

    @Inject(method = "initializeGame", at = @At("HEAD"))
    private void earlyInitializeStream(CallbackInfo ci) {
        twitchStreamProvider = new NullTwitchStream(null);
    }

    /**
     * @author Crosby
     * @reason Force {@link NullTwitchStream} and remove logged error.
     */
    @Overwrite
    private void initializeStream() {}

    /**
     * @author Crosby
     * @reason Dont handle stream key inputs.
     */
    @SuppressWarnings("ConstantConditions")
    @Overwrite
    public void handleKeyInput() {
        int i = Keyboard.getEventKey() == 0 ? Keyboard.getEventCharacter() : Keyboard.getEventKey();

        if (i == 0 || Keyboard.isRepeatEvent()) return;
        if (currentScreen instanceof ControlsOptionsScreen && ((ControlsOptionsScreen) currentScreen).time <= getTime() - 20L) return;
        if (!Keyboard.getEventKeyState()) return;

        if (i == this.options.fullscreenKey.getCode()) {
            this.toggleFullscreen();
        } else if (i == this.options.screenshotKey.getCode()) {
            this.inGameHud.getChatHud().addMessage(ScreenshotUtils.saveScreenshot(this.runDirectory, this.width, this.height, this.fbo));
        }
    }
}
