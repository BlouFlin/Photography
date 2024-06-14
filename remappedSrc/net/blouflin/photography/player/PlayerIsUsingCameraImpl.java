package net.blouflin.photography.player;

public class PlayerIsUsingCameraImpl implements PlayerIsUsingCamera {
    private boolean isUsingPhotographyCamera = false;
    private String handUsingPhotographyCamera;

    @Override
    public boolean isUsingPhotographyCamera() {
        return (isUsingPhotographyCamera);
    }

    @Override
    public String handUsingPhotographyCamera() {
        return handUsingPhotographyCamera;
    }

    @Override
    public void setUsingPhotographyCamera(boolean isUsingPhotographyCamera, String handUsingPhotographyCamera) {
        this.isUsingPhotographyCamera = isUsingPhotographyCamera;
        this.handUsingPhotographyCamera = handUsingPhotographyCamera;
    }
}
