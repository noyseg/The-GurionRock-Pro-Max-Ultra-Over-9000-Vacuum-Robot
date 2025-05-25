package bgu.spl.mics.application.objects;

/**
 * LidarConfigurations represents a single LiDAR device's configuration, including its unique 
 * identifier and the frequency at which it captures or processes data.
 * Used for JSON reading 
 */
public class LidarConfigurations {
    
    private final int id;
    private final int frequency;

    /**
     * Parameterized constructor to initialize LiDAR data.
     *
     * @param id        The unique identifier for the LiDAR.
     * @param frequency The frequency of data capture or processing.
     */
    public LidarConfigurations(int id, int frequency) {
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
     * @return The frequency of data capture or processing.
     */
    public int getFrequency() {
        return frequency;
    }

    @Override
    public String toString() {
        return "LidarData{" +
                "id=" + id +
                ", frequency=" + frequency +
                '}';
    }
}
