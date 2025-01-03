package bgu.spl.mics.application.objects;


/**
 * Represents the configuration details of a camera, including its unique ID,
 * data capture frequency, and camera key (name). 
 * Used for JSON reading
 */
public class CamerasConfigurations {

    private final int id;
    private final int frequency;
    private final String camera_key;

    /**
     * Parameterized constructor to initialize CameraData.
     *
     * @param id        The unique identifier for the camera.
     * @param frequency The frequency of data capture.
     * @param camera_key The key or name of the camera.
     */
    public CamerasConfigurations(int id, int frequency, String camera_key) {
        this.id = id;
        this.frequency = frequency;
        this.camera_key = camera_key;
    }

    /**
     * @return The unique identifier for the camera.
     */
    public int getId() {
        return id;
    }

    /**
     * @return The frequency of data capture.
     */
    public int getFrequency() {
        return frequency;
    }

    /**
     * @return The key or name of the camera.
     */
    public String getCameraKey() {
        return camera_key;
    }

    @Override
    public String toString() {
        return "CamerasConfigurations{" +
                "id=" + id +
                ", frequency=" + frequency +
                ", cameraKey='" + camera_key + '\'' +
                '}';
    }
}
