package net.blouflin.photography.mixin;

import net.blouflin.photography.client.PhotographyHud;
import net.blouflin.photography.networking.CreateMapStatePayload;
import net.blouflin.photography.networking.SetUsingPhotographyCameraPayload;
import net.blouflin.photography.player.PlayerIsUsingCamera;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.*;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(SpyglassItem.class)
public abstract class SpyglassItemMixin {

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void injected(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {

        cir.setReturnValue(new TypedActionResult<>(ActionResult.PASS, user.getStackInHand(hand)));

        if (world.isClient) {

            boolean isPhotographyCamera = false;
            String toContain = "isPhotographyCamera:1b";
            MinecraftClient client = MinecraftClient.getInstance();

            if (user.getStackInHand(hand).getComponents().contains(DataComponentTypes.CUSTOM_DATA)) {
                isPhotographyCamera = user.getStackInHand(hand).getComponents().get(DataComponentTypes.CUSTOM_DATA).toString().contains(toContain);
            }

            if (isPhotographyCamera) {
                if (client.options.getPerspective().isFirstPerson()) {
                    if (PhotographyHud.isUsingPhotographyCamera) {
                        if (Objects.equals(PhotographyHud.handUsingPhotographyCamera, hand.toString())) {
                            if (PhotographyHud.canTakePhoto) {
                                PhotographyHud.canTakePhoto = false;
                                PhotographyHud.isTakingPhoto = true;
                                CreateMapStatePayload payload = new CreateMapStatePayload();
                                ClientPlayNetworking.send(payload);
                            }
                        }
                    } else {
                        PhotographyHud.zoomAmount = 1.0f;
                        PhotographyHud.handUsingPhotographyCamera = hand.toString();
                        PhotographyHud.defaultMouseSensitivity = client.options.getMouseSensitivity().getValue();
                        PhotographyHud.isHUDhidden = client.options.hudHidden;
                        client.options.hudHidden = true;
                        PhotographyHud.isUsingPhotographyCamera = true;
                        user.playSound(SoundEvents.ITEM_SPYGLASS_USE, 1.0f, 1.0f);
                        SetUsingPhotographyCameraPayload payload = new SetUsingPhotographyCameraPayload(PhotographyHud.isUsingPhotographyCamera, PhotographyHud.handUsingPhotographyCamera);
                        ClientPlayNetworking.send(payload);
                    }
                }
            } else {
                user.playSound(SoundEvents.ITEM_SPYGLASS_USE, 1.0f, 1.0f);
                user.incrementStat(Stats.USED.getOrCreateStat(Items.SPYGLASS));
                cir.setReturnValue(ItemUsage.consumeHeldItem(world, user, hand));
            }
        } else {
            boolean isPhotographyCamera = false;
            String toContain = "isPhotographyCamera:1b";

            if (user.getStackInHand(hand).getComponents().contains(DataComponentTypes.CUSTOM_DATA)) {
                isPhotographyCamera = user.getStackInHand(hand).getComponents().get(DataComponentTypes.CUSTOM_DATA).toString().contains(toContain);
            }
            if (!isPhotographyCamera) {
                if (!((PlayerIsUsingCamera) user).isUsingPhotographyCamera()) {
                    user.playSound(SoundEvents.ITEM_SPYGLASS_USE, 1.0f, 1.0f);
                    user.incrementStat(Stats.USED.getOrCreateStat(Items.SPYGLASS));
                    cir.setReturnValue(ItemUsage.consumeHeldItem(world, user, hand));
                }
            }
        }
    }
}
