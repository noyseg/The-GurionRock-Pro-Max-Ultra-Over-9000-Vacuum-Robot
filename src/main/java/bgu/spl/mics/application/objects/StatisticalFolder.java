package bgu.spl.mics.application.objects;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Holds statistical information about the system's operation.
 * This class aggregates metrics such as the runtime of the system,
 * the number of objects detected and tracked, and the number of landmarks identified.
 */
public class StatisticalFolder {
    private final AtomicInteger systemRunTime;
    private final AtomicInteger numDetectedObjects;
    private final AtomicInteger numTrackedObjects;
    private final AtomicInteger numLandmarks;
    private ArrayList<LandMark> landMarks;

    // Singleton instance
    private static class StatisticalFolderHolder {
        private static final StatisticalFolder instance = new StatisticalFolder();
    }

    // Private constructor for singleton pattern
    private StatisticalFolder() {
        this.systemRunTime = new AtomicInteger(0);
        this.numDetectedObjects = new AtomicInteger(0);
        this.numTrackedObjects = new AtomicInteger(0);
        this.numLandmarks = new AtomicInteger(0);
        this.landMarks = null;
    }

    // Public method to get the singleton instance
    public static StatisticalFolder getInstance() {
        return StatisticalFolderHolder.instance;
    }

    // Update system run time using CAS
    public void incrementSystemRunTime(int delta) {
        systemRunTime.addAndGet(delta);
    }

    // Get current system run time
    public int getSystemRunTime() {
        return systemRunTime.get();
    }

    // Update detected objects count using CAS
    public void incrementDetectedObjects(int delta) {
        numDetectedObjects.addAndGet(delta);
    }

    // Get current detected objects count
    public int getNumDetectedObjects() {
        return numDetectedObjects.get();
    }

    // Update tracked objects count using CAS
    public void incrementTrackedObjects(int delta) {
        numTrackedObjects.addAndGet(delta);
    }

    // Get current tracked objects count
    public int getNumTrackedObjects() {
        return numTrackedObjects.get();
    }

    // Update landmarks count using CAS
    public void incrementLandmarks(int delta) {
        numLandmarks.addAndGet(delta);
    }

    // Get current landmarks count
    public int getNumLandmarks() {
        return numLandmarks.get();
    }

    public void setLandMarkslist (ArrayList<LandMark> landMarks){
        this.landMarks = landMarks;
    }


    public ArrayList<LandMark> getLandMarks(){
        return this.landMarks;
    }
}
