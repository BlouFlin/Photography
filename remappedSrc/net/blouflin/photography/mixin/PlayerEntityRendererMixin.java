package net.blouflin.photography.mixin;

import net.blouflin.photography.player.PlayerIsUsingCamera;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(PlayerEntityRenderer.class)
public class PlayerEntityRendererMixin {

    @Inject(method = "getArmPose", at = @At(value = "HEAD"), cancellable = true)
    private static void injected(AbstractClientPlayerEntity player, Hand hand, CallbackInfoReturnable<BipedEntityModel.ArmPose> cir) {
        if (((PlayerIsUsingCamera) player).isUsingPhotographyCamera() && Objects.equals(hand.toString(), ((PlayerIsUsingCamera) player).handUsingPhotographyCamera())) {
            cir.setReturnValue(BipedEntityModel.ArmPose.SPYGLASS);
        }
    }
}
