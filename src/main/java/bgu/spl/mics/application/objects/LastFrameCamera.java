package bgu.spl.mics.application.objects;

import java.util.Collections;
import java.util.List;

public class LastFrameCamera {

    private final String name;
    private final int lastFrameTime;
    private final List<DetectedObject> lastDetectedObj;

    /**
     * Constructor to initialize a LastFrameCamera.
     *
     * @param name            The name of the camera.
     * @param lastFrameTime   The timestamp of the last frame.
     * @param lastDetectedObj The list of detected objects in the last frame.
     */
    public LastFrameCamera(String name, int lastFrameTime, List<DetectedObject> lastDetectedObj) {
        this.name = name;
        this.lastFrameTime = lastFrameTime;
//        this.lastDetectedObj = lastDetectedObj != null ? List.copyOf(lastDetectedObj) : Collections.emptyList();
        this.lastDetectedObj = lastDetectedObj;
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
    public List<DetectedObject> getLastDetectedObj() {
        return lastDetectedObj;
    }

    @Override
    public String toString() {
        return "LastFrameCamera{" +
                "name='" + name + '\'' +
                ", lastFrameTime=" + lastFrameTime +
                ", lastDetectedObj=" + lastDetectedObj +
                '}';
    }
}
