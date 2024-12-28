package bgu.spl.mics.application.objects;

/**
 * Holds statistical information about the system's operation.
 * This class aggregates metrics such as the runtime of the system,
 * the number of objects detected and tracked, and the number of landmarks
 * identified.
 */
public class StatisticalFolder {
    // TODO: Define fields and methods for statistics tracking.
    private static class StatisticalFolderHolder {
        private static final StatisticalFolder instance = new StatisticalFolder();
    }

    // Fields for statistics tracking
    private int systemRunTime;
    private int numDetectedObjects;
    private int numTrackedObjects;
    private int numLandmarks;

    private StatisticalFolder() {
        systemRunTime = 0;
        numDetectedObjects = 0;
        numTrackedObjects = 0;
        numLandmarks = 0;
    }

    public static StatisticalFolder getInstance() {
        return StatisticalFolderHolder.instance;
    }

    public synchronized void addDetectedObjects(int count) {
        numDetectedObjects += count;
    }

    public synchronized void addSystemRunTime(int runtime) {
        systemRunTime += runtime;
    }

    public synchronized void addTrackedObjects(int count) {
        numTrackedObjects += count;
    }

    public synchronized void addLandmarks(int count) {
        numLandmarks += count;
    }

    public synchronized int getSystemRunTime() {
        return systemRunTime;
    }

    public synchronized int getNumDetectedObjects() {
        return numDetectedObjects;
    }

    public synchronized int getNumTrackedObjects() {
        return numTrackedObjects;
    }

    public synchronized int getNumLandmarks() {
        return numLandmarks;
    }

}
