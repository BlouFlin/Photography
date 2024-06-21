package net.blouflin.photography.mixin;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.fabricmc.fabric.impl.registry.sync.RegistrySyncManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerConfigurationNetworkHandler;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(RegistrySyncManager.class)
public class RegistrySyncManagerMixin {

    @Inject(method = "configureClient", at = @At(value = "INVOKE", target = "Lnet/fabricmc/fabric/impl/registry/sync/RegistrySyncManager;createAndPopulateRegistryMap()Ljava/util/Map;"), cancellable = true)
    private static void disableRegistryManager(ServerConfigurationNetworkHandler handler, MinecraftServer server, CallbackInfo ci) {

        ci.cancel();

        final Map<Identifier, Object2IntMap<Identifier>> map = RegistrySyncManager.createAndPopulateRegistryMap();

        if (map == null) {
            // Don't send when there is nothing to map
            return;
        }

        map.get(Identifier.of("minecraft","sound_event")).remove(Identifier.of("photography","camera_shutter"));

        handler.addTask(new RegistrySyncManager.SyncConfigurationTask(handler, map));
    }
}
