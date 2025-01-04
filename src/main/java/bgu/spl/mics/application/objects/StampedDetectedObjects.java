package bgu.spl.mics.application.objects;

import java.util.LinkedList;
import java.util.List;

/**
 * Represents objects detected by the camera at a specific timestamp.
 * Includes the time of detection and a list of detected objects.
 */
public class StampedDetectedObjects {

    private final int time;
    private final List<DetectedObject> detectedObjects;

    /**
     * Default constructor that initializes the time to 0 and creates an empty list
     * of detected objects.
     */
    public StampedDetectedObjects() {
        this.time = 0;
        this.detectedObjects = new LinkedList<>();
    }

    /**
     * Constructor that initializes the time and the list of detected objects.
     *
     * @param time             The time when the objects were detected.
     * @param detectedObjects The list of objects detected at the given time.
     */
    public StampedDetectedObjects(int time, List<DetectedObject> detectedObjects) {
        this.time = time;
        this.detectedObjects = detectedObjects;
    }

    /**
     * @return The list of detected objects.
     */
    public List<DetectedObject> getDetectedObjects() {
        return detectedObjects;
    }

    /**
     * @return The time of detection for the detected objects.
     */
    public int getTime() {
        return time;
    }
}
