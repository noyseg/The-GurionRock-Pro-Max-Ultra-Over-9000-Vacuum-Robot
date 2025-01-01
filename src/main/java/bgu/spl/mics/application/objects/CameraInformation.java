package bgu.spl.mics.application.objects;

public class CameraInformation {

    private int id;
    private int frequency;
    private String cameraKey;

    /**
     * Parameterized constructor to initialize CameraData.
     *
     * @param id        The unique identifier for the camera.
     * @param frequency The frequency of data capture.
     * @param cameraKey The key or name of the camera.
     */
    public CameraInformation(int id, int frequency, String cameraKey) {
        this.id = id;
        this.frequency = frequency;
        this.cameraKey = cameraKey;
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
        return cameraKey;
    }

    /**
     * Sets the key or name of the camera.
     *
     * @param cameraKey The key or name to set.
     */
    public void setCameraKey(String cameraKey) {
        this.cameraKey = cameraKey;
    }

    @Override
    public String toString() {
        return "CameraInformation{" +
                "id=" + id +
                ", frequency=" + frequency +
                ", cameraKey='" + cameraKey + '\'' +
                '}';
    }
}
