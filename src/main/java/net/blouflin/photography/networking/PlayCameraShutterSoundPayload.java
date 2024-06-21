package net.blouflin.photography.networking;

import net.blouflin.photography.Photography;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.GlobalPos;

public record PlayCameraShutterSoundPayload(GlobalPos globalPos) implements CustomPayload {
    public static final CustomPayload.Id<PlayCameraShutterSoundPayload> ID = CustomPayload.id("photography:play_camera_shutter_sound");
    public static final PacketCodec<PacketByteBuf, PlayCameraShutterSoundPayload> CODEC = PacketCodec.of((value, buf) -> buf.writeGlobalPos(value.globalPos), buf -> new PlayCameraShutterSoundPayload(buf.readGlobalPos()));

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void receive(MinecraftClient client, GlobalPos globalPos) {
        client.execute(() -> {
            client.world.playSound(globalPos.pos().getX(),globalPos.pos().getY(),globalPos.pos().getZ(),Photography.CAMERA_SHUTTER,SoundCategory.PLAYERS,0.7f,1.0f,true);
        });
    }
}
