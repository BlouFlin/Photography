package net.blouflin.photography.mixin;

import net.blouflin.photography.client.PhotographyHud;
import net.minecraft.client.Keyboard;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public class KeyboardMixin {

    @Inject(method = "onKey", at = @At("HEAD"), cancellable = true)
    private void injected(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
        if (PhotographyHud.isUsingPhotographyCamera) {
            if (key == GLFW.GLFW_KEY_ESCAPE) {
                PhotographyHud.isUsingPhotographyCamera = false;
                PhotographyHud.stopRenderPhotographyCameraOverlay();
                ci.cancel();
            } else if (key == GLFW.GLFW_KEY_F1) {
                ci.cancel();
            }
        }
    }
}
