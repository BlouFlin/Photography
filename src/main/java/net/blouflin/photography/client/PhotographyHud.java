package net.blouflin.photography.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.blouflin.photography.networking.SetUsingPhotographyCameraPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Colors;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

import java.util.concurrent.CompletableFuture;

public class PhotographyHud {

    public static boolean isUsingPhotographyCamera = false;
    public static float spyglassFlashOpacity = 0.0f;
    public static float spyglassScale = 0.5f;
    public static boolean canTakePhoto = false;
    public static boolean isTakingPhoto = false;
    public static boolean isHUDhidden;
    public static String handUsingPhotographyCamera = Hand.MAIN_HAND.name();
    public static double zoomAmount;
    public static double defaultMouseSensitivity;
    public static final Identifier SPYGLASS_SCOPE = new Identifier("minecraft:textures/misc/spyglass_scope.png");
    public static final Identifier SPYGLASS_SCOPE_CLEAR = new Identifier("photography:textures/misc/spyglass_scope_clear.png");
    public static final Identifier SPYGLASS_SCOPE_FLASH = new Identifier("photography:textures/misc/spyglass_scope_flash.png");
    public static Identifier SPYGLASS_SCOPE_TO_RENDER = SPYGLASS_SCOPE;
    private static final MinecraftClient client = MinecraftClient.getInstance();
    private static final KeyBinding escapeKeybinding = new KeyBinding("key.keyboard.escape", GLFW.GLFW_KEY_ESCAPE, KeyBinding.UI_CATEGORY);

    private static CompletableFuture<Void> screenshotFuture;
    public static void setScreenshotFuture(CompletableFuture<Void> future) {
        screenshotFuture = future;
    }

    public static void renderPhotographyCameraOverlay(DrawContext context) {

        float f = client.getLastFrameDuration();
        spyglassScale = MathHelper.lerp(0.5f * f, spyglassScale, 1.125f);

        if (client.options.getPerspective().isFirstPerson() && client.currentScreen == null) {
            client.options.hudHidden = true;
            checkIsPhotographyCameraOpen(client);
            renderSpyglassOverlay(context, spyglassScale);
            spyglassFlashOpacity = MathHelper.lerp(0.1f * f, spyglassFlashOpacity, 0.025f);

            if (spyglassScale >= 1.1f && spyglassFlashOpacity <= 0.1f && !isTakingPhoto) {
                canTakePhoto = true;
            } else {
                canTakePhoto = false;
            }

            if (escapeKeybinding.isPressed()) {
                stopRenderPhotographyCameraOverlay();
            }
        } else {
            stopRenderPhotographyCameraOverlay();
        }

        if (screenshotFuture != null) {
            screenshotFuture.complete(null);
            screenshotFuture = null;
        }
    }

    public static void stopRenderPhotographyCameraOverlay() {
        client.options.getMouseSensitivity().setValue(defaultMouseSensitivity);
        client.options.hudHidden = isHUDhidden;
        spyglassFlashOpacity = 0.0f;
        spyglassScale = 0.5f;
        canTakePhoto = false;
        zoomAmount = 1.0f;
        PhotographyHud.isUsingPhotographyCamera = false;
        client.player.playSound(SoundEvents.ITEM_SPYGLASS_STOP_USING, 1.0f, 1.0f);
        SetUsingPhotographyCameraPayload payload = new SetUsingPhotographyCameraPayload(isUsingPhotographyCamera, handUsingPhotographyCamera);
        ClientPlayNetworking.send(payload);
    }

    public static void checkIsPhotographyCameraOpen(MinecraftClient client) {
        boolean isPhotographyCamera = false;
        String toContain = "isPhotographyCamera:1b";
        PlayerEntity player = client.player;
        Hand hand = Hand.valueOf(handUsingPhotographyCamera);
        if (player.getStackInHand(hand).getComponents().contains(DataComponentTypes.CUSTOM_DATA)) {
            isPhotographyCamera = player.getStackInHand(hand).getComponents().get(DataComponentTypes.CUSTOM_DATA).toString().contains(toContain);
        }
        if (!isPhotographyCamera) {
            if (PhotographyHud.isUsingPhotographyCamera) {
                stopRenderPhotographyCameraOverlay();
            }
        }
    }

    private static void renderSpyglassOverlay(DrawContext context, float scale) {
        float f;
        float g = f = (float)Math.min(context.getScaledWindowWidth(), context.getScaledWindowHeight());
        float h = Math.min((float)context.getScaledWindowWidth() / f, (float)context.getScaledWindowHeight() / g) * scale;
        int i = MathHelper.floor(f * h);
        int j = MathHelper.floor(g * h);
        int k = (context.getScaledWindowWidth() - i) / 2;
        int l = (context.getScaledWindowHeight() - j) / 2;
        int m = k + i;
        int n = l + j;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        context.drawTexture(SPYGLASS_SCOPE_TO_RENDER, k, l, -90, 0.0f, 0.0f, i, j, i, j);

        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, spyglassFlashOpacity);
        context.drawTexture(SPYGLASS_SCOPE_FLASH, k, l, -90, 0.0f, 0.0f, i, j, i, j);

        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);

        RenderSystem.disableBlend();

        context.fill(RenderLayer.getGuiOverlay(), 0, n, context.getScaledWindowWidth(), context.getScaledWindowHeight(), -90, Colors.BLACK);
        context.fill(RenderLayer.getGuiOverlay(), 0, 0, context.getScaledWindowWidth(), l, -90, Colors.BLACK);
        context.fill(RenderLayer.getGuiOverlay(), 0, l, k, n, -90, Colors.BLACK);
        context.fill(RenderLayer.getGuiOverlay(), m, l, context.getScaledWindowWidth(), n, -90, Colors.BLACK);
    }
}