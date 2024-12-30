package bgu.spl.mics.application.objects;

import java.util.Collections;
import java.util.List;

public class LastFrameLidar {

    private final String name;
    private final int lastFrameTime;
    private final List<TrackedObject> lastTrackedObjects;

    /**
     * Constructor to initialize a LastFrameCamera.
     *
     * @param name            The name of the camera.
     * @param lastFrameTime   The timestamp of the last frame.
     * @param lastDetectedObj The list of detected objects in the last frame.
     */
    public LastFrameLidar(String name, int lastFrameTime, List<TrackedObject> lastTrackedObjects) {
        this.name = name;
        this.lastFrameTime = lastFrameTime;
        this.lastTrackedObjects = lastTrackedObjects != null ? List.copyOf(lastTrackedObjects) : Collections.emptyList();
    }

    /**
     * @return The name of the camera.
     */
    public String getName() {
        return name;
    }

    /**
     * @return The timestamp of the last frame.
     */
    public int getLastFrameTime() {
        return lastFrameTime;
    }

    /**
     * @return An unmodifiable list of detected objects in the last frame.
     */
    public List<TrackedObject> getLastDetectedObj() {
        return lastTrackedObjects;
    }

    @Override
    public String toString() {
        return "lastLiDarWorkerTrackersFrame{" +
                "name='" + name + '\'' +
                ", lastFrameTime=" + lastFrameTime +
                ", lastTrackedObjects=" + lastTrackedObjects +
                '}';
    }
}

