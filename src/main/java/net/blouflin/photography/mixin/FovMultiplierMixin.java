package net.blouflin.photography.mixin;

import net.blouflin.photography.client.PhotographyHud;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractClientPlayerEntity.class)
public class FovMultiplierMixin {

    @Inject(method = "getFovMultiplier", at = @At("HEAD"), cancellable = true)
    private void injected(CallbackInfoReturnable<Float> cir) {
        if (MinecraftClient.getInstance().options.getPerspective().isFirstPerson() && PhotographyHud.isUsingPhotographyCamera) {
            cir.setReturnValue(0.5f * (float) PhotographyHud.zoomAmount);
        }
    }
}
