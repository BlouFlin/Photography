package net.blouflin.photography.networking;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.CustomModelDataComponent;
import net.minecraft.component.type.MapIdComponent;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Unit;

import java.util.Objects;

public record SpawnPicturePayload(Integer id, NbtCompound nbtCompound) implements CustomPayload {
    public static final CustomPayload.Id<SpawnPicturePayload> ID = CustomPayload.id("photography:spawn_picture");
    public static final PacketCodec<PacketByteBuf, SpawnPicturePayload> CODEC = PacketCodec.of((value, buf) -> buf.writeInt(value.id).writeNbt(value.nbtCompound), buf -> new SpawnPicturePayload(buf.readInt(),buf.readNbt()));

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void receive(ServerPlayerEntity player, Integer id, NbtCompound nbtCompound) {

        MapIdComponent mapId = new MapIdComponent(id);
        RegistryWrapper.WrapperLookup registryLookup = player.getRegistryManager();
        MapState mapState = MapState.fromNbt(nbtCompound, registryLookup);

        player.server.execute(() -> {

            ItemStack stack = new ItemStack(Items.FILLED_MAP);
            player.getEntityWorld().putMapState(mapId, mapState);
            stack.set(DataComponentTypes.MAP_ID, mapId);
            stack.apply(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT, comp -> comp.apply(currentNbt -> {
                currentNbt.putBoolean("isPhotographyFilledMap",true);
            }));
            stack.set(DataComponentTypes.CUSTOM_MODEL_DATA, new CustomModelDataComponent(56776));
            stack.set(DataComponentTypes.ITEM_NAME, Text.literal("Photography"));
            stack.set(DataComponentTypes.HIDE_ADDITIONAL_TOOLTIP, Unit.INSTANCE);

            if(!player.isCreative()) {
                ItemStack itemStack = new ItemStack(Items.FILLED_MAP);
                itemStack.apply(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT, comp -> comp.apply(currentNbt -> {
                    currentNbt.putBoolean("isPhotographyEmptyMap",true);
                }));
                itemStack.set(DataComponentTypes.CUSTOM_MODEL_DATA, new CustomModelDataComponent(56775));
                itemStack.set(DataComponentTypes.HIDE_ADDITIONAL_TOOLTIP, Unit.INSTANCE);
                itemStack.set(DataComponentTypes.ITEM_NAME, Text.literal("Photographic Paper"));

                int slot = player.getInventory().getSlotWithStack(itemStack);
                if(slot != -1) {
                    convertStack(player, slot, stack);
                } else if (player.getStackInHand(Hand.OFF_HAND).getItem() == itemStack.getItem()) {
                    if (Objects.equals(player.getStackInHand(Hand.OFF_HAND).getComponents().get(DataComponentTypes.CUSTOM_DATA), itemStack.getComponents().get(DataComponentTypes.CUSTOM_DATA))) {
                        convertStack(player, 40, stack);
                    }
                }
            }
            else {
                if (player.getInventory().insertStack(stack)) {
                    player.getInventory().insertStack(stack);
                } else {
                    ItemEntity itemEntity = new ItemEntity(player.getServerWorld(), player.getPos().x, player.getPos().y, player.getPos().z, stack);
                    player.getServerWorld().spawnEntity(itemEntity);
                }
            }
        });
    }

    private static void convertStack(ServerPlayerEntity player, int slot, ItemStack stack) {
        player.getInventory().getStack(slot).decrement(1);

        if (player.getInventory().insertStack(stack)) {
            player.getInventory().insertStack(stack);
        } else {
            ItemEntity itemEntity = new ItemEntity(player.getServerWorld(), player.getPos().x, player.getPos().y, player.getPos().z, stack);
            player.getServerWorld().spawnEntity(itemEntity);
        }
    }
}
