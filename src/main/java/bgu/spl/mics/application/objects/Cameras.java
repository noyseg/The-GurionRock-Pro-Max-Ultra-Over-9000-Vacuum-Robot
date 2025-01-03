package bgu.spl.mics.application.objects;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import bgu.spl.mics.application.messages.CrashedBroadcast;

public class Cameras {

    private List<CamerasConfigurations> CamerasConfigurations;
    private String camera_datas_path;

    /**
     * Constructor to initialize with a list of cameras and optional metadata.
     *
     * @param Cameras The list of cameras.
     * @param cameraData Metadata or description about the cameras.
     */
    public Cameras(List<CamerasConfigurations> CamerasConfigurations, String camera_datas_path) {
        this.CamerasConfigurations = CamerasConfigurations; // Create a copy for immutability
        this.camera_datas_path = camera_datas_path;
    }

    /**
     * @return The list of all cameras.
     */
    public List<CamerasConfigurations> getAllCameras() {
        return this.CamerasConfigurations; // Return a copy for immutability
    }

    /**
     * Sets the list of all cameras.
     *
     * @param Cameras The new list of cameras.
     */
    public void setAllCameras(List<CamerasConfigurations> CamerasConfigurations) {
        this.CamerasConfigurations = CamerasConfigurations; // Create a copy for immutability
    }

    /**
     * Adds a new camera to the list.
     *
     * @param cameraData The camera to add.
     */
    public void addCamera(CamerasConfigurations cameraData) {
        CamerasConfigurations.add(cameraData);
    }

    /**
     * Removes a camera from the list.
     *
     * @param cameraData The camera to remove.
     */
    public void removeCamera(CamerasConfigurations camera) {
        CamerasConfigurations.remove(camera);
    }

    /**
     * @return Metadata or description about the cameras.
     */
    public String getCameraData() {
        return camera_datas_path;
    }

    /**
     * Sets metadata or description about the cameras.
     *
     * @param cameraData The metadata to set.
     */
    public void setCameraData(String camera_datas_path) {
        this.camera_datas_path = camera_datas_path;
    }

    @Override
    public String toString() {
        return "Cameras{" +
                "allCameras=" + CamerasConfigurations +
                ", cameraData='" + camera_datas_path + '\'' +
                '}';
    }
}