package bgu.spl.mics.application.objects;

public class CamerasConfigurations {

    private int id;
    private int frequency;
    private String camera_key;

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
     * Sets the unique identifier for the camera.
     *
     * @param id The unique identifier to set.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return The frequency of data capture.
     */
    public int getFrequency() {
        return frequency;
    }

    /**
     * Sets the frequency of data capture.
     *
     * @param frequency The frequency to set.
     */
    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    /**
     * @return The key or name of the camera.
     */
    public String getCameraKey() {
        return camera_key;
    }

    /**
     * Sets the key or name of the camera.
     *
     * @param camera_key The key or name to set.
     */
    public void setCameraKey(String camera_key) {
        this.camera_key = camera_key;
    }

    @Override
    public String toString() {
        return "CameraInformation{" +
                "id=" + id +
                ", frequency=" + frequency +
                ", cameraKey='" + camera_key + '\'' +
                '}';
    }
}
