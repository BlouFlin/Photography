package net.blouflin.photography.mixin;

import net.blouflin.photography.player.PlayerIsUsingCamera;
import net.blouflin.photography.player.PlayerIsUsingCameraImpl;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin implements PlayerIsUsingCamera {
    @Unique
    private final PlayerIsUsingCameraImpl playerExtension = new PlayerIsUsingCameraImpl();

    @Override
    public boolean isUsingPhotographyCamera() {
        return playerExtension.isUsingPhotographyCamera();
    }

    @Override
    public String handUsingPhotographyCamera() {
        return playerExtension.handUsingPhotographyCamera();
    }

    @Override
    public void setUsingPhotographyCamera(boolean isUsingPhotographyCamera, String handUsingPhotographyCamera) {
        playerExtension.setUsingPhotographyCamera(isUsingPhotographyCamera, handUsingPhotographyCamera);
    }
}
