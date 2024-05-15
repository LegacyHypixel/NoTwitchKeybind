package io.github.racoondog.notwitchkeybind.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.NullTwitchStream;
import net.minecraft.client.util.TwitchStreamProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
    @Shadow private TwitchStreamProvider twitchStreamProvider;

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
}
