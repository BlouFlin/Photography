package net.blouflin.photography.mixin;

import net.blouflin.photography.client.PhotographyHud;
import net.minecraft.entity.player.PlayerInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerInventory.class)
public class PlayerInventoryMixin {
    @Inject(at = @At("HEAD"), method = "scrollInHotbar(D)V", cancellable = true)
    private void onScrollInHotbar(double scrollAmount, CallbackInfo ci) {
        if(PhotographyHud.isUsingPhotographyCamera)
            ci.cancel();
    }
}
