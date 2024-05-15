package io.github.racoondog.notwitchkeybind.mixin;

import com.google.common.collect.Lists;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Environment(EnvType.CLIENT)
@Mixin(GameOptions.class)
public abstract class GameOptionsMixin {
    @Shadow public KeyBinding[] allKeys;

    @Shadow public KeyBinding streamStartStopKey;
    @Shadow public KeyBinding streamPauseUnpauseKey;
    @Shadow public KeyBinding streamCommercialKey;
    @Shadow public KeyBinding streamToggleMicKey;

    @Inject(method = "<init>*", at = @At("TAIL"))
    private void removeKeybinds(CallbackInfo ci) {
        // remove keys from configs
        List<KeyBinding> newKeys = Lists.newArrayList(allKeys);
        newKeys.remove(streamStartStopKey);
        newKeys.remove(streamPauseUnpauseKey);
        newKeys.remove(streamCommercialKey);
        newKeys.remove(streamToggleMicKey);
        allKeys = newKeys.toArray(new KeyBinding[0]);

        // prevent keys from being usable
        streamStartStopKey.setCode(-1);
        streamPauseUnpauseKey.setCode(-1);
        streamCommercialKey.setCode(-1);
        streamToggleMicKey.setCode(-1);

        // rebuild categories
        KeyBinding.getCategories().clear();
        for (KeyBinding keyBinding : allKeys) {
            KeyBinding.getCategories().add(keyBinding.getCategory());
        }
    }
}
