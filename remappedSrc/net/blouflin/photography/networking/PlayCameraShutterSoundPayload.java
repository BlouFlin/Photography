package net.blouflin.photography.networking;

import net.blouflin.photography.Photography;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.sound.SoundCategory;

import java.util.UUID;

public record PlayCameraShutterSoundPayload(UUID player) implements CustomPayload {
    public static final Id<PlayCameraShutterSoundPayload> ID = CustomPayload.id("photography:play_camera_shutter_sound");
    public static final PacketCodec<PacketByteBuf, PlayCameraShutterSoundPayload> CODEC = PacketCodec.of((value, buf) -> buf.writeUuid(value.player), buf -> new PlayCameraShutterSoundPayload(buf.readUuid()));

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void receive(MinecraftClient client, UUID player) {
        client.execute(() -> {
            PlayerEntity playerEntity = client.world.getPlayerByUuid(player);
            client.world.playSound(client.player,playerEntity.getBlockPos(),Photography.CAMERA_SHUTTER, SoundCategory.PLAYERS, 0.7f,1.0f);
        });
    }
}
