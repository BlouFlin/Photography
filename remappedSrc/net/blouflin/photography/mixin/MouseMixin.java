package net.blouflin.photography.mixin;

import net.blouflin.photography.client.PhotographyHud;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public class MouseMixin {

    @Inject(at = @At("RETURN"), method = "onMouseScroll(JDD)V")
    private void onMouseScroll(long window, double horizontal, double vertical, CallbackInfo ci) {

        MinecraftClient client = MinecraftClient.getInstance();
        vertical = -vertical;

        if(PhotographyHud.isUsingPhotographyCamera) {
            if (vertical > 0) {
                PhotographyHud.zoomAmount *= 1.1;
            } else if (vertical < 0) {
                PhotographyHud.zoomAmount *= 0.9;
            }
            PhotographyHud.zoomAmount = MathHelper.clamp(PhotographyHud.zoomAmount, 0.2, 2);

            client.options.getMouseSensitivity().setValue(PhotographyHud.defaultMouseSensitivity * (PhotographyHud.zoomAmount / 2));
        }
    }
}