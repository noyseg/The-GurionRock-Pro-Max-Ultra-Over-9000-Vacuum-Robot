package bgu.spl.mics.application.objects;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class ErrorCoordinator {
    private static class ErrorCoordinatorHolder {
        private static ErrorCoordinator instance = new ErrorCoordinator();
    }

    private HashMap<String,StampedDetectedObjects> lastFramesCameras;
    private HashMap<String,List<TrackedObject>> lastFramesLidars;
    private List<Pose> robotPoses;
    private Object lockLastFramesCameras;
    private Object lockLastFramesLidars;
    private boolean isCrashed = false;
    private String description = "" ;
    private String faultSensor = "";
    private int crashedTick = -1; 


     // Private constructor for Singleton pattern
     private ErrorCoordinator() {
        this.lastFramesCameras = new HashMap<>();
        this.lastFramesLidars = new HashMap<>();
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
    public void setLastFramesCameras(String cameraName,StampedDetectedObjects lastDetectedObjects) {
        synchronized (lockLastFramesCameras) {
            lastFramesCameras.put(cameraName, lastDetectedObjects);
        }
    }

    /**
     * Adds a new frame to the list of last frames from LiDARs.
     *
     * @param newFrame The frame to add.
     */
    public void setLastFramesLidars(String lidarName,List<TrackedObject> lastTrackedObject) {
        synchronized (lockLastFramesLidars) {
            lastFramesLidars.put(lidarName, lastTrackedObject);
        }
    }

    /**
     * Retrieves the last frames from cameras.
     *
     * @return last frames from cameras.
     */
    public HashMap<String,StampedDetectedObjects> getLastFramesCameras() {
        return this.lastFramesCameras;
    }

    /**
     * Retrieves the last frames from LiDARs.
     *
     * @return A copy of the last frames from LiDARs.
     */
    public HashMap<String,List<TrackedObject>> getLastFramesLidars() {
        return this.lastFramesLidars;
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
    