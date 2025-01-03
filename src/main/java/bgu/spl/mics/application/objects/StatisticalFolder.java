package bgu.spl.mics.application.objects;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Holds statistical information about the system's operation.
 * This class aggregates metrics such as the runtime of the system,
 * the number of objects detected and tracked, and the number of landmarks identified.
 */
public class StatisticalFolder {
    // Singleton instance
    private static class StatisticalFolderHolder {
        private static final StatisticalFolder instance = new StatisticalFolder();
    }
    private final AtomicInteger systemRunTime; // Tracks the system's runtime
    private final AtomicInteger numDetectedObjects; // Tracks the number of detected objects
    private final AtomicInteger numTrackedObjects; // Tracks the number of tracked objects
    private final AtomicInteger numLandmarks; // Tracks the number of landmarks
    private ArrayList<LandMark> landMarks; // List of identified landmarks

    /**
     * Private constructor for singleton pattern
     */
    private StatisticalFolder() {
        this.systemRunTime = new AtomicInteger(0);
        this.numDetectedObjects = new AtomicInteger(0);
        this.numTrackedObjects = new AtomicInteger(0);
        this.numLandmarks = new AtomicInteger(0);
        this.landMarks = null; // In case of error, sets it when creating output file 
    }

     /**
     * Public method to get the singleton instance of the StatisticalFolder.
     *
     * @return The StatisticalFolder singleton instance.
     */
    public static StatisticalFolder getInstance() {
        return StatisticalFolderHolder.instance;
    }
    
    /**
     * Retrieves the current system runtime.
     *
     * @return The current system runtime.
     */
    public int getSystemRunTime() {
        return systemRunTime.get();
    }

    /**
     * Retrieves the current count of detected objects.
     *
     * @return The current count of detected objects.
     */
    public int getNumDetectedObjects() {
        return numDetectedObjects.get();
    }
    
    /**
     * Retrieves the current count of tracked objects.
     *
     * @return The current count of tracked objects.
     */
    public int getNumTrackedObjects() {
        return numTrackedObjects.get();
    }

    /**
     * Retrieves the current count of landmarks.
     *
     * @return The current count of landmarks.
     */
    public int getNumLandmarks() {
        return numLandmarks.get();
    }

    public ArrayList<LandMark> getLandMarks(){
        return this.landMarks;
    }

    /**
     * Increments the system runtime by the specified delta using Compare-And-Swap (CAS).
     * This ensures thread-safety when modifying the system runtime value.
     *
     * @param delta The amount by which to increment the system runtime.
     */
    public void incrementSystemRunTime(int delta) {
        systemRunTime.addAndGet(delta);
    }

    /**
     * Increments the number of detected objects by the specified delta using CAS.
     * This ensures thread-safety when modifying the count of detected objects.
     *
     * @param delta The amount by which to increment the detected objects count.
     */
    public void incrementDetectedObjects(int delta) {
        numDetectedObjects.addAndGet(delta);
    }

    /**
     * Increments the number of tracked objects by the specified delta using CAS.
     * This ensures thread-safety when modifying the count of tracked objects.
     *
     * @param delta The amount by which to increment the tracked objects count.
     */
    public void incrementTrackedObjects(int delta) {
        numTrackedObjects.addAndGet(delta);
    }

    /**
     * Increments the number of landmarks by the specified delta using CAS.
     * This ensures thread-safety when modifying the count of landmarks.
     *
     * @param delta The amount by which to increment the landmarks count.
     */
    public void incrementLandmarks(int delta) {
        numLandmarks.addAndGet(delta);
    }

    /**
     * Sets the list of identified landmarks.
     *
     * @param landMarks The list of landmarks to be set.
     */
    public void setLandMarkslist(ArrayList<LandMark> landMarks) {
        this.landMarks = landMarks;
    }
}
