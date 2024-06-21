package net.blouflin.photography.networking;

import net.blouflin.photography.player.PlayerIsUsingCamera;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;

public record SetUsingPhotographyCameraPayload(Boolean isUsingPhotographyCamera, String handUsingPhotographyCamera) implements CustomPayload {
    public static final CustomPayload.Id<SetUsingPhotographyCameraPayload> ID = CustomPayload.id("photography:set_using_photography_camera");
    public static final PacketCodec<PacketByteBuf, SetUsingPhotographyCameraPayload> CODEC = PacketCodec.of((value, buf) -> buf.writeBoolean(value.isUsingPhotographyCamera).writeString(value.handUsingPhotographyCamera), buf -> new SetUsingPhotographyCameraPayload(buf.readBoolean(),buf.readString()));

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void receive(ServerPlayerEntity player, Boolean isUsingPhotographyCamera, String handUsingPhotographyCamera) {
        player.server.execute(() -> {

            ((PlayerIsUsingCamera) player).setUsingPhotographyCamera(isUsingPhotographyCamera,handUsingPhotographyCamera);
            if (isUsingPhotographyCamera) {
                player.playSound(SoundEvents.ITEM_SPYGLASS_USE, 1.0f, 1.0f);
            } else {
                player.playSound(SoundEvents.ITEM_SPYGLASS_STOP_USING, 1.0f, 1.0f);
            }
            for (ServerPlayerEntity otherPlayer : player.server.getPlayerManager().getPlayerList()) {
                GetUsingPhotographyCameraPayload payload = new GetUsingPhotographyCameraPayload(player.getUuid(),isUsingPhotographyCamera,handUsingPhotographyCamera);
                ServerPlayNetworking.send(otherPlayer,payload);
            }
        });
    }
}
