package com.lion.graveyard.mixin.client;

import com.lion.graveyard.Graveyard;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.sounds.SoundSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SoundEngine.class)
public abstract class SoundEngineMixin {
    @Unique
    private static final ThreadLocal<Float> graveyard$factor = ThreadLocal.withInitial(() -> 1.0F);

    @Inject(method = "play", at = @At("HEAD"))
    private void graveyard$readPlayedSound(SoundInstance sound, CallbackInfoReturnable<SoundEngine.PlayResult> cir) {
        graveyard$factor.set(graveyard$factorOf(sound));
    }

    @Inject(method = "calculateVolume(Lnet/minecraft/client/resources/sounds/SoundInstance;)F", at = @At("HEAD"))
    private void graveyard$readTickedSound(SoundInstance sound, CallbackInfoReturnable<Float> cir) {
        graveyard$factor.set(graveyard$factorOf(sound));
    }

    @Inject(method = "calculateVolume(FLnet/minecraft/sounds/SoundSource;)F", at = @At("RETURN"), cancellable = true)
    private void graveyard$scaleVolume(float volume, SoundSource source, CallbackInfoReturnable<Float> cir) {
        cir.setReturnValue(cir.getReturnValueF() * graveyard$factor.get());
    }

    @Unique
    private static float graveyard$factorOf(SoundInstance sound) {
        if (!Graveyard.MOD_ID.equals(sound.getIdentifier().getNamespace())) {
            return 1.0F;
        }

        return (float) Graveyard.getConfig().number("soundVolume", 1.0D);
    }
}
