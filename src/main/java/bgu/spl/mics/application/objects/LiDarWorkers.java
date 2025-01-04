package bgu.spl.mics.application.objects;

import java.util.List;


/**
 * LiDarWorkers represents a collection of LiDAR configurations along with the path of the LiDAR data.
 * Used for JSON reading
 */
public class LiDarWorkers {

    private final List<LidarConfigurations> LidarConfigurations;
    private final String lidars_data_path;

    /**
     * Constructor to initialize with a list of LiDARs and a data path.
     *
     * @param lidars        The list of LiDARs.
     * @param lidarDataPath The path or description of LiDAR data.
     */
    public LiDarWorkers(List<LidarConfigurations> LidarConfigurations, String lidarDataPath) {
        this.LidarConfigurations = LidarConfigurations; 
        this.lidars_data_path = lidarDataPath;
    }

    /**
     * @return The list of all LiDARs.
     */
    public List<LidarConfigurations> getLidars() {
        return this.LidarConfigurations;
    }

    /**
     * @return The path or description of the LiDAR data.
     */
    public String getLidarDataPath() {
        return lidars_data_path;
    }

    @Override
    public String toString() {
        return "Lidars{" +
                "lidars=" + LidarConfigurations +
                ", lidarDataPath='" + lidars_data_path + '\'' +
                '}';
    }
    
}
