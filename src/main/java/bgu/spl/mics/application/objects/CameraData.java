package bgu.spl.mics.application.objects;

import java.util.ArrayList;
import java.util.List;

public class CameraData {

    private String camera_key;
    private List<StampedDetectedObjects> stampedDetected; 

    /**
     * Parameterized constructor to initialize the camera data.
     *
     * @param cameraName      The name of the camera.
     * @param stampedDetected The list of stamped detected objects.
     */
    public CameraData(String cameraName, List<StampedDetectedObjects> stampedDetected) {
        this.camera_key = cameraName;
        this.stampedDetected = new ArrayList<>(stampedDetected); // Create a copy for immutability
    }

    /**
     * @return The name of the camera.
     */
    public String getCameraName() {
        return camera_key;
    }

    /**
     * Sets the name of the camera.
     *
     * @param cameraName The name to set.
     */
    public void setCameraName(String cameraName) {
        this.camera_key = cameraName;
    }

    /**
     * @return The list of stamped detected objects.
     */
    public List<StampedDetectedObjects> getStampedDetected() {
        return this.stampedDetected; // Return a copy for immutability
    }

    /**
     * Sets the list of stamped detected objects.
     *
     * @param stampedDetected The list to set.
     */
    public void setStampedDetected(List<StampedDetectedObjects> stampedDetected) {
        this.stampedDetected = stampedDetected; // Create a copy for immutability
    }

    /**
     * Adds a stamped detected object to the list.
     *
     * @param stampedDetectedObject The object to add.
     */
    public void addStampedDetectedObject(StampedDetectedObjects stampedDetectedObject) {
        stampedDetected.add(stampedDetectedObject);
    }

    /**
     * Removes a stamped detected object from the list.
     *
     * @param stampedDetectedObject The object to remove.
     */
    public void removeStampedDetectedObject(StampedDetectedObjects stampedDetectedObject) {
        stampedDetected.remove(stampedDetectedObject);
    }

    @Override
    public String toString() {
        return "CameraData{" +
                "cameraName='" + camera_key + '\'' +
                ", stampedDetected=" + stampedDetected +
                '}';
    }
}