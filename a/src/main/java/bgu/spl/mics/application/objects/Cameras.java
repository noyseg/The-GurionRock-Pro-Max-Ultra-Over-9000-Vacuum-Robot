package bgu.spl.mics.application.objects;
import java.util.List;

/**
 * Represents a collection of cameras with their configurations and data location.
 * Used for JSON reading
 */
public class Cameras {

    private List<CamerasConfigurations> CamerasConfigurations;
    private String camera_datas_path;

    /**
     * Constructor to initialize with a list of camera configurations and the data path.
     *
     * @param camerasConfigurations The list of camera configurations.
     * @param camera_datas_path        The file path for camera data.
     */
    public Cameras(List<CamerasConfigurations> CamerasConfigurations, String camera_datas_path) {
        this.CamerasConfigurations = CamerasConfigurations; // Create a copy for immutability
        this.camera_datas_path = camera_datas_path;
    }

    /**
     * @return A list of all camera configurations.
     */
    public List<CamerasConfigurations> getAllCameras() {
        return this.CamerasConfigurations; // Return a copy for immutability
    }

    /**
     * Sets the list of all camera configurations.
     *
     * @param camerasConfigurations The new list of camera configurations.
     */
    public void setAllCameras(List<CamerasConfigurations> CamerasConfigurations) {
        this.CamerasConfigurations = CamerasConfigurations; // Create a copy for immutability
    }

   /**
     * Adds a new camera configuration to the list.
     *
     * @param cameraConfiguration The camera configuration to add.
     */
    public void addCamera(CamerasConfigurations cameraConfiguration) {
        CamerasConfigurations.add(cameraConfiguration);
    }


    /**
     * Removes a camera configuration from the list.
     *
     * @param cameraConfiguration The camera configuration to remove.
     */
    public void removeCamera(CamerasConfigurations cameraConfiguration) {
        CamerasConfigurations.remove(cameraConfiguration);
    }

    /**
     * @return The file path to the camera data.
     */
    public String getCameraDataPath() {
        return camera_datas_path;
    }

     /**
     * Sets the file path to the camera data.
     *
     * @param cameraDataPath The new file path to set.
     */
    public void setCameraDataPath(String cameraDataPath) {
        this.camera_datas_path = cameraDataPath;
    }

    @Override
    public String toString() {
        return "Cameras{" +
                "camerasConfigurations=" + CamerasConfigurations +
                ", cameraDataPath='" + camera_datas_path + '\'' +
                '}';
    }
}