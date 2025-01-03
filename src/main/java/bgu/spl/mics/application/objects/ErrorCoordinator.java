package bgu.spl.mics.application.objects;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Manages the error state of the system, specifically when a sensor failure or crash occurs.
 * It stores the last frames from cameras and LiDARs, the robot poses, and provides synchronization
 * and access to these elements. Implements the Singleton pattern to ensure only one instance exists.
 */
public class ErrorCoordinator {
    // Singleton instance holder
    private static class ErrorCoordinatorHolder {
        private static ErrorCoordinator instance = new ErrorCoordinator();
    }

    private final HashMap<String, StampedDetectedObjects> lastFramesCameras; // Stores the last frames detected by cameras
    private final HashMap<String, List<TrackedObject>> lastFramesLidars; // Stores the last frames tracked by LiDARs
    private final List<Pose> robotPoses; // Stores the robot's poses
    private final Object lockLastFramesCameras; // Lock object for synchronizing camera frame access
    private final Object lockLastFramesLidars; // Lock object for synchronizing LiDAR frame access
    private boolean isCrashed = false; // Flag to track if the system has crashed
    private String description = ""; // Description of the error reason
    private String faultSensor = ""; // The name of the faulty sensor
    private int crashedTick = -1; // The tick when the crash occurred


    /**
     * Private constructor to initialize the ErrorCoordinator instance.
     * Initializes collections for last frames from cameras and LiDARs, and robot poses.
     */
     private ErrorCoordinator() {
        this.lastFramesCameras = new HashMap<>();
        this.lastFramesLidars = new HashMap<>();
        this.robotPoses = new LinkedList<>();
        this.lockLastFramesCameras = new Object();
        this.lockLastFramesLidars = new Object();
    }

    /**
     * Public method to get the Singleton instance of the ErrorCoordinator.
     *
     * @return The ErrorCoordinator singleton instance.
     */
    public static ErrorCoordinator getInstance() {
        return ErrorCoordinatorHolder.instance;
    }

    /**
     * Retrieves the last frames from cameras.
     *
     * @return The map of last frames detected by cameras.
     */
    public HashMap<String, StampedDetectedObjects> getLastFramesCameras() {
        return this.lastFramesCameras;
    }

    /**
     * Retrieves the last frames from LiDARs.
     *
     * @return The map of last frames detected by LiDARs.
     */
    public HashMap<String, List<TrackedObject>> getLastFramesLidars() {
        return this.lastFramesLidars;
    }

    /**
     * Retrieves the list of robot poses.
     *
     * @return The list of robot poses.
     */
    public List<Pose> getRobotPoses() {
        return this.robotPoses;
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

    /**
     * Retrieves the description of the crash or error.
     *
     * @return The crash description.
     */
    public String getDescription() {
        return this.description;
    }

     /**
     * Adds a new frame to the list of last frames from cameras.
     * Synchronized to ensure thread safety when updating the frames.
     *
     * @param cameraName The name of the camera.
     * @param lastDetectedObjects The detected objects in the current camera frame.
     */
    public void setLastFramesCameras(String cameraName,StampedDetectedObjects lastDetectedObjects) {
        synchronized (lockLastFramesCameras) {
            lastFramesCameras.put(cameraName, lastDetectedObjects);
        }
    }

     /**
     * Adds a new frame to the list of last frames from LiDARs.
     * Synchronized to ensure thread safety when updating the frames.
     *
     * @param lidarName The name of the LiDAR.
     * @param lastTrackedObject The tracked objects in the current LiDAR frame.
     */
    public void setLastFramesLidars(String lidarName,List<TrackedObject> lastTrackedObject) {
        synchronized (lockLastFramesLidars) {
            lastFramesLidars.put(lidarName, lastTrackedObject);
        }
    }

    /**
     * Sets the system as crashed and records the details of the crash.
     * This method only allows setting the crash status once.
     *
     * @param faultSensor The name of the sensor that caused the crash.
     * @param crashedTick The tick when the crash occurred.
     * @param description A description of the error reason.
     */
    public synchronized void setCrashed(String faultSensor,int crashedTick, String description) {
        if (!isCrashed){
            this.description = description;
            this.faultSensor = faultSensor;
            this.crashedTick = crashedTick;
            isCrashed = true;
        }
    }

    /**
     * Adds a new pose to the list of robot poses.
     *
     * @param pose The new pose to be added.
     */
    public void setRobotPoses(Pose pose) {
        robotPoses.add(pose);
    }
}
    