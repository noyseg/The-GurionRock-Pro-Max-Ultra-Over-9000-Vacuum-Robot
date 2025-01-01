package bgu.spl.mics.application.objects;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Cameras {

    private List<CameraInformation> allCameras;
    private String cameraData;

    /**
     * Default constructor initializes an empty list of cameras.
     */
    public Cameras() {
        this.allCameras = new ArrayList<>();
        this.cameraData = "";
    }

    /**
     * Constructor to initialize with a list of cameras and optional metadata.
     *
     * @param allCameras The list of cameras.
     * @param cameraData Metadata or description about the cameras.
     */
    public Cameras(List<CameraInformation> allCameras, String cameraData) {
        this.allCameras = allCameras; // Create a copy for immutability
        this.cameraData = cameraData;
    }

    /**
     * @return The list of all cameras.
     */
    public List<CameraInformation> getAllCameras() {
        return this.allCameras; // Return a copy for immutability
    }

    /**
     * Sets the list of all cameras.
     *
     * @param allCameras The new list of cameras.
     */
    public void setAllCameras(List<CameraInformation> allCameras) {
        this.allCameras = allCameras; // Create a copy for immutability
    }

    /**
     * Adds a new camera to the list.
     *
     * @param cameraData The camera to add.
     */
    public void addCamera(CameraInformation cameraData) {
        allCameras.add(cameraData);
    }

    /**
     * Removes a camera from the list.
     *
     * @param cameraData The camera to remove.
     */
    public void removeCamera(CameraInformation cameraData) {
        allCameras.remove(cameraData);
    }

    /**
     * @return Metadata or description about the cameras.
     */
    public String getCameraData() {
        return cameraData;
    }

    /**
     * Sets metadata or description about the cameras.
     *
     * @param cameraData The metadata to set.
     */
    public void setCameraData(String cameraData) {
        this.cameraData = cameraData;
    }

    @Override
    public String toString() {
        return "Cameras{" +
                "allCameras=" + allCameras +
                ", cameraData='" + cameraData + '\'' +
                '}';
    }
}