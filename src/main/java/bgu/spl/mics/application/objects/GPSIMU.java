package bgu.spl.mics.application.objects;

import java.util.List;

/**
 * Represents the robot's GPS and IMU system.
 * Provides information about the robot's position and movement.
 */
public class GPSIMU {
    private int currentTick; // The current simulation tick
    private STATUS status; // The status of the GPSIMU system (UP or DOWN)
    private final List<Pose> PoseList; // List of poses representing robot's positions at various ticks
    private int numOfCameras; // Number of cameras in the system
    private int numOfLidars; // Number of LiDAR sensors in the system

    /**
     * Constructor to initialize the GPSIMU with the current tick, a list of poses,
     * and the counts of cameras and lidars.
     *
     * @param currentTick The current simulation tick.
     * @param PoseList The list of poses representing robot's positions.
     * @param numOfCameras The number of cameras in the system.
     * @param numOfLidars The number of LiDAR sensors in the system.
     */
    public GPSIMU(int currentTick, List<Pose> PoseList, int numOfCameras, int numOfLidars){
        this.currentTick = currentTick;
        this.status = STATUS.UP;
        this.PoseList = PoseList;
        this.numOfCameras = numOfCameras;
        this.numOfLidars = numOfLidars;
    }

     /**
     * @return The current simulation time (tick).
     */
    public int getCurrentTime(){
        return this.currentTick;
    }

    /**
     * @return The current status of the GPSIMU system (e.g., UP or DOWN).
     */
    public STATUS getStatus(){
        return this.status;
    }

    /**
     * @return The list of poses representing the robot's positions at various simulation ticks.
     */
    public List<Pose> getPoseList(){
        return this.PoseList;
    }

    /**
     * @return The pose at the current tick.
     */
    public Pose getPose(){
        if (PoseList.size() >= currentTick){
            return this.PoseList.get(currentTick-1);
        }
        return null;
    }

    /**
     * @return The current number of cameras in the system.
     */
    public int getCameraCount(){
        return this.numOfCameras;
    }

    /**
     * @return The current number of LiDARs in the system.
     */
    public int getLidarCount(){
        return this.numOfLidars;
    }

    /**
     * Updates the current simulation tick.
     *
     * @param updateTick The new simulation tick.
     */
    public void setCurrentTick(int updateTick){
        this.currentTick = updateTick;
   }

   /**
    * Updates the status of the GPSIMU system.
    *
    * @param status The new status (e.g., UP or DOWN).
    */
   public void setStatus(STATUS status){
       this.status = status;
   }

    /**
     * Updates the last recorded pose in the ErrorCoordinator.
     * The last pose will be stored for potential error recovery.
     */
    public void updateLastPose(){
        ErrorCoordinator.getInstance().setRobotPoses(getPose());
    }

    /**
     * Decreases the camera count by one (removes one camera).
     */
    public void decrementCameraCount(){
        this.numOfCameras -= 1;
    }

    /**
     * Decreases the LiDAR count by one (removes one LiDAR).
     */
    public void decrementLidarCount(){
        this.numOfLidars -= 1;
    }
    
}
