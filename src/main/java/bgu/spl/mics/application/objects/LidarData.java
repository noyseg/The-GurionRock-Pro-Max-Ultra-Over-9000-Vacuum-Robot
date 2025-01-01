package bgu.spl.mics.application.objects;

public class LidarData {
    
    private int id;
    private int frequency;

    /**
     * Parameterized constructor to initialize LiDAR data.
     *
     * @param id        The unique identifier for the LiDAR.
     * @param frequency The frequency of data capture or processing.
     */
    public LidarData(int id, int frequency) {
        this.id = id;
        this.frequency = frequency;
    }

    /**
     * @return The unique identifier for the LiDAR.
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the unique identifier for the LiDAR.
     *
     * @param id The unique identifier to set.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return The frequency of data capture or processing.
     */
    public int getFrequency() {
        return frequency;
    }

    /**
     * Sets the frequency of data capture or processing.
     *
     * @param frequency The frequency to set.
     */
    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    @Override
    public String toString() {
        return "LidarData{" +
                "id=" + id +
                ", frequency=" + frequency +
                '}';
    }
}
