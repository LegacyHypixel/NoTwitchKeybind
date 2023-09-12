package io.github.racoondog.notwitchkeybind.mixin;

import com.google.common.collect.Lists;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Mixin(GameSettings.class)
public abstract class GameOptionsMixin {
    @Shadow public KeyBinding[] keyBindings;

    @Shadow public KeyBinding keyBindStreamStartStop;
    @Shadow public KeyBinding keyBindStreamPauseUnpause;
    @Shadow public KeyBinding keyBindStreamCommercials;
    @Shadow public KeyBinding keyBindStreamToggleMic;

    @Inject(method = "loadOptions", at = @At("HEAD"))
    private void removeKeybinds(CallbackInfo ci) {
        List<KeyBinding> newKeys = Lists.newArrayList(keyBindings);
        newKeys.remove(keyBindStreamStartStop);
        newKeys.remove(keyBindStreamPauseUnpause);
        newKeys.remove(keyBindStreamCommercials);
        newKeys.remove(keyBindStreamToggleMic);
        keyBindings = newKeys.toArray(new KeyBinding[0]);

        Set<String> newCategories = new HashSet<>();
        for (KeyBinding keyBinding : newKeys) {
            newCategories.add(keyBinding.getKeyCategory());
        }

        KeyBinding.getKeybinds().clear();
        KeyBinding.getKeybinds().addAll(newCategories);
    }
}
