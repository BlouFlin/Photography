package net.blouflin.photography;

import net.blouflin.photography.client.PhotographyHud;
import net.blouflin.photography.networking.CreatePicturePayload;
import net.blouflin.photography.networking.GetUsingPhotographyCameraPayload;
import net.blouflin.photography.networking.PlayCameraShutterSoundPayload;
import net.blouflin.photography.player.PlayerIsUsingCamera;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.*;

import java.util.Objects;

@Environment(EnvType.CLIENT)
public class PhotographyClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {

        ModelLoadingPlugin.register(pluginContext -> pluginContext.addModels(Identifier.of("photography","item/camera")));

        PayloadTypeRegistry.playS2C().register(CreatePicturePayload.ID, CreatePicturePayload.CODEC);
        ClientPlayNetworking.registerGlobalReceiver(CreatePicturePayload.ID, (payload, handler) -> CreatePicturePayload.receive(handler.client(), payload.id(), payload.nbtCompound()));

        PayloadTypeRegistry.playS2C().register(GetUsingPhotographyCameraPayload.ID, GetUsingPhotographyCameraPayload.CODEC);
        ClientPlayNetworking.registerGlobalReceiver(GetUsingPhotographyCameraPayload.ID, (payload, handler) -> GetUsingPhotographyCameraPayload.receive(handler.client(), payload.player(), payload.isUsingPhotographyCamera(), payload.handUsingPhotographyCamera()));

        PayloadTypeRegistry.playS2C().register(PlayCameraShutterSoundPayload.ID, PlayCameraShutterSoundPayload.CODEC);
        ClientPlayNetworking.registerGlobalReceiver(PlayCameraShutterSoundPayload.ID, (payload, handler) -> PlayCameraShutterSoundPayload.receive(handler.client(), payload.player()));

        HudRenderCallback.EVENT.register(this::onHudRender);

        ItemStack photographyCameraStack = new ItemStack(Items.SPYGLASS);
        photographyCameraStack.apply(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT, comp -> comp.apply(currentNbt -> {
            currentNbt.putBoolean("isPhotographyCamera",true);
        }));

        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (((PlayerIsUsingCamera) player).isUsingPhotographyCamera()) {
                player.getStackInHand(Hand.valueOf(PhotographyHud.handUsingPhotographyCamera)).use(world, player, Hand.valueOf(PhotographyHud.handUsingPhotographyCamera));
                return ActionResult.FAIL;
            } else if (player.getStackInHand(Hand.MAIN_HAND).getItem() == photographyCameraStack.getItem()) {
                if (Objects.equals(player.getStackInHand(Hand.MAIN_HAND).getComponents().get(DataComponentTypes.CUSTOM_DATA), photographyCameraStack.getComponents().get(DataComponentTypes.CUSTOM_DATA))) {
                    player.getStackInHand(Hand.MAIN_HAND).use(world, player, Hand.MAIN_HAND);
                    return ActionResult.FAIL;
                }
            }
            return ActionResult.PASS;
        });

        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (((PlayerIsUsingCamera) player).isUsingPhotographyCamera()) {
                player.getStackInHand(Hand.valueOf(PhotographyHud.handUsingPhotographyCamera)).use(world, player, Hand.valueOf(PhotographyHud.handUsingPhotographyCamera));
                return ActionResult.FAIL;
            } else if (player.getStackInHand(Hand.MAIN_HAND).getItem() == photographyCameraStack.getItem()) {
                if (Objects.equals(player.getStackInHand(Hand.MAIN_HAND).getComponents().get(DataComponentTypes.CUSTOM_DATA), photographyCameraStack.getComponents().get(DataComponentTypes.CUSTOM_DATA))) {
                    player.getStackInHand(Hand.MAIN_HAND).use(world, player, Hand.MAIN_HAND);
                    return ActionResult.FAIL;
                }
            }
            return ActionResult.PASS;
        });

        UseItemCallback.EVENT.register((player, world, hand) -> {
            if (((PlayerIsUsingCamera) player).isUsingPhotographyCamera()) {
                player.getStackInHand(Hand.valueOf(PhotographyHud.handUsingPhotographyCamera)).use(world, player, Hand.valueOf(PhotographyHud.handUsingPhotographyCamera));
                return TypedActionResult.fail(ItemStack.EMPTY);
            } else if (player.getStackInHand(Hand.MAIN_HAND).getItem() == photographyCameraStack.getItem()) {
                if (Objects.equals(player.getStackInHand(Hand.MAIN_HAND).getComponents().get(DataComponentTypes.CUSTOM_DATA), photographyCameraStack.getComponents().get(DataComponentTypes.CUSTOM_DATA))) {
                    player.getStackInHand(Hand.MAIN_HAND).use(world, player, Hand.MAIN_HAND);
                    return TypedActionResult.fail(ItemStack.EMPTY);
                }
            }
            return TypedActionResult.pass(ItemStack.EMPTY);
        });
    }

    private void onHudRender(DrawContext context, RenderTickCounter renderTickCounter) {
        if (PhotographyHud.isUsingPhotographyCamera) {
            PhotographyHud.renderPhotographyCameraOverlay(context);
        }
    }
}
