package net.blouflin.photography.networking;

import net.blouflin.image2map.Image2Map;
import net.blouflin.image2map.renderer.MapRenderer;
import net.blouflin.photography.client.PhotographyHud;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.ScreenshotRecorder;
import net.minecraft.item.map.MapState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.registry.RegistryWrapper;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public record CreatePicturePayload(Integer id, NbtCompound nbtCompound) implements CustomPayload {
    public static final CustomPayload.Id<CreatePicturePayload> ID = CustomPayload.id("photography_create_picture");
    public static final PacketCodec<PacketByteBuf, CreatePicturePayload> CODEC = PacketCodec.of((value, buf) -> buf.writeInt(value.id).writeNbt(value.nbtCompound), buf -> new CreatePicturePayload(buf.readInt(),buf.readNbt()));

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void receive(MinecraftClient client, Integer id, NbtCompound nbtCompound) {

        CompletableFuture<Void> future = new CompletableFuture<>();

        client.execute(() -> {

            RegistryWrapper.WrapperLookup registryLookup = client.player.getRegistryManager();
            MapState mapState = MapState.fromNbt(nbtCompound, registryLookup);

            PhotographyHud.CAMERA_SCOPE_TO_RENDER = PhotographyHud.CAMERA_SCOPE_CLEAR;

            PhotographyHud.setScreenshotFuture(future);

            future.thenRun(() -> {
                NativeImage nativeImage = ScreenshotRecorder.takeScreenshot(client.getFramebuffer());

                PhotographyHud.CAMERA_SCOPE_TO_RENDER = PhotographyHud.CAMERA_SCOPE;
                PhotographyHud.spyglassFlashOpacity = 1.0f;
                PhotographyHud.isTakingPhoto = false;

                try {
                    byte[] imageBytes = nativeImage.getBytes();
                    BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(imageBytes));
                    bufferedImage = CreatePicturePayload.crop(bufferedImage, bufferedImage.getHeight(), bufferedImage.getHeight());

                    MapState mapState1  = MapRenderer.render(bufferedImage, Image2Map.DitherMode.FLOYD, id, mapState);

                    SpawnPicturePayload payload = new SpawnPicturePayload(id, nbtCompound);
                    ClientPlayNetworking.send(payload);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        });
    }

    public static BufferedImage crop(BufferedImage bufferedImage, int targetWidth, int targetHeight) throws IOException {
        int height = bufferedImage.getHeight();
        int width = bufferedImage.getWidth();

        // Coordinates of the image's middle
        int xc = (width - targetWidth) / 2;
        int yc = (height - targetHeight) / 2;

        // Crop
        BufferedImage croppedImage = bufferedImage.getSubimage(
                xc,
                yc,
                targetWidth, // width
                targetHeight // height
        );
        return croppedImage;
    }
}