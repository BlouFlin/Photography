package net.blouflin.photography.networking;

import net.blouflin.photography.player.PlayerIsUsingCamera;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

import java.util.UUID;

public record GetUsingPhotographyCameraPayload(UUID player, Boolean isUsingPhotographyCamera, String handUsingPhotographyCamera) implements CustomPayload {
    public static final CustomPayload.Id<GetUsingPhotographyCameraPayload> ID = CustomPayload.id("photography:get_using_photography_camera");
    public static final PacketCodec<PacketByteBuf, GetUsingPhotographyCameraPayload> CODEC = PacketCodec.of((value, buf) -> buf.writeUuid(value.player).writeBoolean(value.isUsingPhotographyCamera).writeString(value.handUsingPhotographyCamera), buf -> new GetUsingPhotographyCameraPayload(buf.readUuid(),buf.readBoolean(),buf.readString()));

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void receive(MinecraftClient client, UUID player, Boolean isUsingPhotographyCamera, String handUsingPhotographyCamera) {
        client.execute(() -> {

            ((PlayerIsUsingCamera) client.world.getPlayerByUuid(player)).setUsingPhotographyCamera(isUsingPhotographyCamera,handUsingPhotographyCamera);
        });
    }
}
