package bgu.spl.mics.application.objects;

/**
 * Represents the configuration file for the simulation.
 * Contains settings for cameras, LiDAR workers, pose data, and simulation timing.
 * Used for JSON reading
 */
public class ConfigFile {
    private final Cameras Cameras;           // Camera configurations
    private final LiDarWorkers LiDarWorkers; // LiDAR worker configurations
    private final String poseJsonFile;       // File path for pose data
    private final int TickTime;              // Time duration for each simulation tick
    private final int Duration;              // Total simulation duration

  /**
     * Parameterized constructor to initialize the configuration.
     *
     * @param Cameras       The camera configuration.
     * @param LiDarWorkers  The lidar configuration.
     * @param poseJsonFile  The file path for pose data.
     * @param TickTime      The time duration for each simulation tick.
     * @param Duration      The total duration of the simulation.
     */
    public ConfigFile(Cameras Cameras, LiDarWorkers LiDarWorkers, String poseJsonFile, int TickTime, int Duration) {
        this.Cameras = Cameras;
        this.LiDarWorkers = LiDarWorkers;
        this.poseJsonFile = poseJsonFile;
        this.TickTime = TickTime;
        this.Duration = Duration;
    }

    /**
     * @return The camera configuration.
     */
    public Cameras getCameras() {
        return Cameras;
    }

    /**
     * @return The lidar configuration.
     */
    public LiDarWorkers getLidars() {
        return LiDarWorkers;
    }

    /**
     * @return The file path for pose data.
     */
    public String getPoseFilePath() {
        return poseJsonFile;
    }

    /**
     * @return The time duration for each simulation tick.
     */
    public int getTickTime() {
        return TickTime;
    }

    /**
     * @return The total duration of the simulation.
     */
    public int getDuration() {
        return Duration;
    }

    @Override
    public String toString() {
        return "Config{" +
                "cameras=" + Cameras +
                ", lidars=" + LiDarWorkers +
                ", poseFilePath='" + poseJsonFile + '\'' +
                ", tickTime=" + TickTime +
                ", duration=" + Duration +
                '}';
    }
}


    
