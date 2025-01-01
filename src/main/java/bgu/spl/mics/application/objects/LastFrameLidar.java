package bgu.spl.mics.application.objects;

import java.util.Collections;
import java.util.List;

public class LastFrameLidar {

    private final String name;
    private final List<TrackedObject> lastTrackedObjects;

    /**
     * Constructor to initialize a LastFrameCamera.
     *
     * @param name            The name of the camera.
     * @param lastDetectedObj The list of detected objects in the last frame.
     */
    public LastFrameLidar(String name, List<TrackedObject> lastTrackedObjects) {
        this.name = name;
        this.lastTrackedObjects = lastTrackedObjects != null ? List.copyOf(lastTrackedObjects) : Collections.emptyList();
    }

    /**
     * @return The name of the camera.
     */
    public String getName() {
        return name;
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
                ", lastTrackedObjects=" + lastTrackedObjects +
                '}';
    }
}

