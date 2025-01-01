package bgu.spl.mics.application.objects;

import java.util.List;

public class LiDarWorkers {

    private List<LidarConfigurations> LidarConfigurations;
    private String lidars_data_path;

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
     * Sets the list of LiDARs.
     *
     * @param lidars The list of LiDARs.
     */
    public void setLidars(List<LidarConfigurations> lidars) {
        this.LidarConfigurations = lidars; 
    }

    /**
     * Adds a new LiDAR to the list.
     *
     * @param lidar The LiDAR to add.
     */
    public void addLidar(LidarConfigurations lidar) {
        LidarConfigurations.add(lidar);
    }

    /**
     * Removes a LiDAR from the list.
     *
     * @param lidar The LiDAR to remove.
     */
    public void removeLidar(LidarConfigurations lidar) {
        LidarConfigurations.remove(lidar);
    }

    /**
     * @return The path or description of the LiDAR data.
     */
    public String getLidarDataPath() {
        return lidars_data_path;
    }

    /**
     * Sets the path or description of the LiDAR data.
     *
     * @param lidarDataPath The new path or description.
     */
    public void setLidarDataPath(String lidarDataPath) {
        this.lidars_data_path = lidarDataPath;
    }

    @Override
    public String toString() {
        return "Lidars{" +
                "lidars=" + LidarConfigurations +
                ", lidarDataPath='" + lidars_data_path + '\'' +
                '}';
    }
    
}
