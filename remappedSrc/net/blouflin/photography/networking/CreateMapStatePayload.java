package net.blouflin.photography.networking;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.map.MapState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;

public record CreateMapStatePayload() implements CustomPayload {
    public static final CustomPayload.Id<CreateMapStatePayload> ID = CustomPayload.id("photography:create_map_state");
    public static final PacketCodec<PacketByteBuf, CreateMapStatePayload> CODEC = PacketCodec.of((value, buf) -> {}, buf -> new CreateMapStatePayload());

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void receive(ServerPlayerEntity player) {

        player.server.execute(() -> {
            int id = player.getEntityWorld().getNextMapId().id();
            NbtCompound nbt = new NbtCompound();
            RegistryWrapper.WrapperLookup registryLookup = player.getRegistryManager();
            nbt.putString("dimension", player.getEntityWorld().getRegistryKey().getValue().toString());
            nbt.putInt("xCenter", (int) player.getX());
            nbt.putInt("zCenter", (int) player.getZ());
            nbt.putBoolean("locked", true);
            nbt.putBoolean("unlimitedTracking", false);
            nbt.putBoolean("trackingPosition", false);
            nbt.putByte("scale", (byte) 3);
            nbt.put("banners", new NbtList());
            nbt.put("frames", new NbtList());
            MapState state = MapState.fromNbt(nbt,registryLookup);

            NbtCompound nbtCompound = new NbtCompound();
            nbtCompound = state.writeNbt(nbtCompound, registryLookup);

            for (ServerPlayerEntity otherPlayer : player.server.getPlayerManager().getPlayerList()) {
                PlayCameraShutterSoundPayload payload = new PlayCameraShutterSoundPayload(player.getUuid());
                ServerPlayNetworking.send(otherPlayer,payload);
            }

            CreatePicturePayload payload = new CreatePicturePayload(id, nbtCompound);
            ServerPlayNetworking.send(player, payload);
        });
    }
}
