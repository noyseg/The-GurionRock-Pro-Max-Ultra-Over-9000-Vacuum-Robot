package bgu.spl.mics.application.objects;

import java.util.LinkedList;
import java.util.List;

public class ErrorCoordinator {
    private static class ErrorCoordinatorHolder {
        private static ErrorCoordinator instance = new ErrorCoordinator();
    }

    private List<LastFrameCamera> lastFramesCameras;
    private List<LastFrameLidar> lastFramesLidars;
    private List<Pose> robotPoses;
    private Object lockLastFramesCameras;
    private Object lockLastFramesLidars;
    private boolean isCrashed = false;
    private String description = "" ;
    private String faultSensor = "";
    private int crashedTick = -1; 


     // Private constructor for Singleton pattern
     private ErrorCoordinator() {
        this.lastFramesCameras = new LinkedList<>();
        this.lastFramesLidars = new LinkedList<>();
        this.robotPoses = new LinkedList<>();
        this.lockLastFramesCameras = new Object();
        this.lockLastFramesLidars = new Object();
    }

    // Public method to get the Singleton instance
    public static ErrorCoordinator getInstance() {
        return ErrorCoordinatorHolder.instance;
    }

    /**
     * Adds a new frame to the list of last frames from cameras.
     *
     * @param newFrame The frame to add.
     */
    public void setLastFramesCameras(LastFrameCamera newFrame) {
        synchronized (lockLastFramesCameras) {
            lastFramesCameras.add(newFrame);
        }
    }

    /**
     * Adds a new frame to the list of last frames from LiDARs.
     *
     * @param newFrame The frame to add.
     */
    public void setLastFramesLidars(LastFrameLidar newFrame) {
        synchronized (lockLastFramesLidars) {
            lastFramesLidars.add(newFrame);
        }
    }

    /**
     * Retrieves the last frames from cameras.
     *
     * @return last frames from cameras.
     */
    public List<LastFrameCamera> getLastFramesCameras() {
        synchronized (lockLastFramesCameras) {
            return this.lastFramesCameras;
        }
    }

    /**
     * Retrieves the last frames from LiDARs.
     *
     * @return A copy of the last frames from LiDARs.
     */
    public List<LastFrameLidar> getLastFramesLidars() {
        synchronized (lockLastFramesLidars) {
            return this.lastFramesLidars;
        }
    }

    public List<Pose> getRobotPoses() {
        return this.robotPoses;
    }

    public synchronized void setCrashed(String faultSensor,int crashedTick, String description) {
        if (!isCrashed){
            this.description = description;
            this.faultSensor = faultSensor;
            this.crashedTick = crashedTick;
            isCrashed = true;
        }
    }

    /**
     * Retrieves the name of the fault sensor.
     *
     * @return The name of the fault sensor.
     */
    public String getFaultSensor() {
        return faultSensor;
    }

    /**
     * Retrieves the tick when the crash occurred.
     *
     * @return The tick when the crash occurred.
     */
    public int getCrashedTick() {
        return crashedTick;
    }

    public void setRobotPoses(Pose pose) {
        robotPoses.add(pose);
    }

    public String getDescription() {
        return this.description;
    }
}
    