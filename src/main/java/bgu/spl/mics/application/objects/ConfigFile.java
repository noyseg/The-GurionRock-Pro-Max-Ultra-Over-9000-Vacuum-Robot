package bgu.spl.mics.application.objects;

public class ConfigFile {
  private Cameras cameras;
  private Lidars lidars;
  private String poseFilePath;
  private int tickTime;
  private int duration;

  /**
     * Parameterized constructor to initialize the configuration.
     *
     * @param cameras       The camera configuration.
     * @param lidars        The lidar configuration.
     * @param poseFilePath  The file path for pose data.
     * @param tickTime      The time duration for each simulation tick.
     * @param duration      The total duration of the simulation.
     */
    public ConfigFile(Cameras cameras, Lidars lidars, String poseFilePath, int tickTime, int duration) {
        this.cameras = cameras;
        this.lidars = lidars;
        this.poseFilePath = poseFilePath;
        this.tickTime = tickTime;
        this.duration = duration;
    }

    /**
     * @return The camera configuration.
     */
    public Cameras getCameras() {
        return cameras;
    }

    /**
     * Sets the camera configuration.
     *
     * @param cameras The camera configuration to set.
     */
    public void setCameras(Cameras cameras) {
        this.cameras = cameras;
    }

    /**
     * @return The lidar configuration.
     */
    public Lidars getLidars() {
        return lidars;
    }

    /**
     * Sets the lidar configuration.
     *
     * @param lidars The lidar configuration to set.
     */
    public void setLidars(Lidars lidars) {
        this.lidars = lidars;
    }

    /**
     * @return The file path for pose data.
     */
    public String getPoseFilePath() {
        return poseFilePath;
    }

    /**
     * Sets the file path for pose data.
     *
     * @param poseFilePath The file path to set.
     */
    public void setPoseFilePath(String poseFilePath) {
        this.poseFilePath = poseFilePath;
    }

    /**
     * @return The time duration for each simulation tick.
     */
    public int getTickTime() {
        return tickTime;
    }

    /**
     * Sets the time duration for each simulation tick.
     *
     * @param tickTime The tick time to set.
     */
    public void setTickTime(int tickTime) {
        this.tickTime = tickTime;
    }

    /**
     * @return The total duration of the simulation.
     */
    public int getDuration() {
        return duration;
    }

    /**
     * Sets the total duration of the simulation.
     *
     * @param duration The duration to set.
     */
    public void setDuration(int duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        return "Config{" +
                "cameras=" + cameras +
                ", lidars=" + lidars +
                ", poseFilePath='" + poseFilePath + '\'' +
                ", tickTime=" + tickTime +
                ", duration=" + duration +
                '}';
    }
}


    
