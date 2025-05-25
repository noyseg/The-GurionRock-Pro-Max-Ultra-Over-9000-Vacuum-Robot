package bgu.spl.mics.application.objects;

import java.util.List;

import bgu.spl.mics.application.messages.PoseEvent;
import bgu.spl.mics.application.messages.TickBroadcast;

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

    /**
     * Processes a tick event, updating the robot's current pose and broadcasting a {@link PoseEvent}.
     * <p>
     * - Sets the current tick in the GPSIMU system.
     * - If a valid pose exists for the current tick, a {@link PoseEvent} is generated and returned.
     * - If no valid pose is found, the GPSIMU status is set to DOWN.
     * </p>
     *
     * @param tick The {@link TickBroadcast} that provides the current tick time.
     * @return A {@link PoseEvent} if a valid pose exists, or {@code null} otherwise.
     */
    public PoseEvent processTick(TickBroadcast tick) {
        if (numOfCameras == 0 && numOfLidars == 0) {
            setStatus(STATUS.DOWN);
        }
        setCurrentTick(tick.getCurrentTime());
        Pose pose = getPose();
        if (pose != null) {
            // Generate and send a PoseEvent if a valid pose is available.
            updateLastPose();
            return new PoseEvent(pose);
        } else {
            // If no pose is found, set GPSIMU status to DOWN and terminate.
            setStatus(STATUS.DOWN);
        }
        return null;
    }
    
}
