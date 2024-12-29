package bgu.spl.mics.application.objects;

import java.util.LinkedList;
import java.util.List;

public class ErrorCoordinator {
    private static class ErrorCoordinatorHolder {
        private static ErrorCoordinator instance = new ErrorCoordinator();
    }
    private List<List<DetectedObject>> lastFramesCameras;
    private List<List<TrackedObject>> lastFramesLidars;
    private Object lockLastFramesCameras;
    private Object lockLastFramesLidars;

     // Private constructor for Singleton pattern
     private ErrorCoordinator() {
        this.lastFramesCameras = new LinkedList<>();
        this.lastFramesLidars = new LinkedList<>();
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
    public void setLastFramesCameras(List<DetectedObject> newFrame) {
        synchronized (lockLastFramesCameras) {
            lastFramesCameras.add(newFrame);
        }
    }

    /**
     * Adds a new frame to the list of last frames from LiDARs.
     *
     * @param newFrame The frame to add.
     */
    public void setLastFramesLidars(List<TrackedObject> newFrame) {
        synchronized (lockLastFramesLidars) {
            lastFramesLidars.add(newFrame);
        }
    }

    /**
     * Retrieves the last frames from cameras.
     *
     * @return A copy of the last frames from cameras.
     */
    public List<List<DetectedObject>> getLastFramesCameras() {
        synchronized (lockLastFramesCameras) {
            return new LinkedList<>(lastFramesCameras);
        }
    }

    /**
     * Retrieves the last frames from LiDARs.
     *
     * @return A copy of the last frames from LiDARs.
     */
    public List<List<TrackedObject>> getLastFramesLidars() {
        synchronized (lockLastFramesLidars) {
            return new LinkedList<>(lastFramesLidars);
        }
    }
}
    