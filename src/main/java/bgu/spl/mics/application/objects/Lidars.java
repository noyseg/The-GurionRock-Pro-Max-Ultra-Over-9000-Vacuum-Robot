package bgu.spl.mics.application.objects;

import java.util.List;

public class Lidars {

    private List<LidarData>lidars;
    private String lidarDataPath;

    /**
     * Constructor to initialize with a list of LiDARs and a data path.
     *
     * @param lidars        The list of LiDARs.
     * @param lidarDataPath The path or description of LiDAR data.
     */
    public Lidars(List<LidarData> lidars, String lidarDataPath) {
        this.lidars = lidars; 
        this.lidarDataPath = lidarDataPath;
    }

    /**
     * @return The list of all LiDARs.
     */
    public List<LidarData> getLidars() {
        return this.lidars;
    }

    /**
     * Sets the list of LiDARs.
     *
     * @param lidars The list of LiDARs.
     */
    public void setLidars(List<LidarData> lidars) {
        this.lidars = lidars; 
    }

    /**
     * Adds a new LiDAR to the list.
     *
     * @param lidar The LiDAR to add.
     */
    public void addLidar(LidarData lidar) {
        lidars.add(lidar);
    }

    /**
     * Removes a LiDAR from the list.
     *
     * @param lidar The LiDAR to remove.
     */
    public void removeLidar(LidarData lidar) {
        lidars.remove(lidar);
    }

    /**
     * @return The path or description of the LiDAR data.
     */
    public String getLidarDataPath() {
        return lidarDataPath;
    }

    /**
     * Sets the path or description of the LiDAR data.
     *
     * @param lidarDataPath The new path or description.
     */
    public void setLidarDataPath(String lidarDataPath) {
        this.lidarDataPath = lidarDataPath;
    }

    @Override
    public String toString() {
        return "Lidars{" +
                "lidars=" + lidars +
                ", lidarDataPath='" + lidarDataPath + '\'' +
                '}';
    }
    
}
