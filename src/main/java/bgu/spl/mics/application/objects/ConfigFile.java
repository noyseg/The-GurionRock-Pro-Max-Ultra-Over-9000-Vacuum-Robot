package bgu.spl.mics.application.objects;

public class ConfigFile {
  private Cameras Cameras;
  private LiDarWorkers LiDarWorkers;
  private String poseJsonFile;
  private int TickTime;
  private int Duration;

  /**
     * Parameterized constructor to initialize the configuration.
     *
     * @param Cameras       The camera configuration.
     * @param LiDarWorkers        The lidar configuration.
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
     * Sets the camera configuration.
     *
     * @param cameras The camera configuration to set.
     */
    public void setCameras(Cameras cameras) {
        this.Cameras = cameras;
    }

    /**
     * @return The lidar configuration.
     */
    public LiDarWorkers getLidars() {
        return LiDarWorkers;
    }

    /**
     * Sets the lidar configuration.
     *
     * @param lidars The lidar configuration to set.
     */
    public void setLidars(LiDarWorkers LiDarWorkers) {
        this.LiDarWorkers = LiDarWorkers;
    }

    /**
     * @return The file path for pose data.
     */
    public String getPoseFilePath() {
        return poseJsonFile;
    }

    /**
     * Sets the file path for pose data.
     *
     * @param poseFilePath The file path to set.
     */
    public void setPoseFilePath(String poseFilePath) {
        this.poseJsonFile = poseFilePath;
    }

    /**
     * @return The time duration for each simulation tick.
     */
    public int getTickTime() {
        return TickTime;
    }

    /**
     * Sets the time duration for each simulation tick.
     *
     * @param tickTime The tick time to set.
     */
    public void setTickTime(int tickTime) {
        this.TickTime = tickTime;
    }

    /**
     * @return The total duration of the simulation.
     */
    public int getDuration() {
        return Duration;
    }

    /**
     * Sets the total duration of the simulation.
     *
     * @param duration The duration to set.
     */
    public void setDuration(int duration) {
        this.Duration = duration;
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


    
