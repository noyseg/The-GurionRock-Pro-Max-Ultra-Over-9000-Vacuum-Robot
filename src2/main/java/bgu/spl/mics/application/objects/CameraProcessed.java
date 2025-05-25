package bgu.spl.mics.application.objects;

/**
 * Represents a processed detection by a camera.
 *
 * Purpose:
 * - Encapsulates the data of detected objects along with the time at which 
 *   the processing was completed.
 *
 * Usage:
 * - Used in the `CameraService` to manage detections waiting to be sent 
 *   to other system components (e.g., LiDAR services).
 *
 * Key Features:
 * - Stores the processing time, which is the tick time when the detection is ready.
 * - Provides access to the detected objects through a `StampedDetectedObjects` instance.
 */
public class CameraProcessed {
    private final int processionTime; // The tick time when the detected objects are ready for further processing or transmission.
    private final StampedDetectedObjects detectedObjects; // The detected objects associated with this processing,
    // including their details and the original detection time.

    /**
     * Constructs a `CameraProcessed` object.
     *
     * @param time            The tick time when the detection is ready for further processing.
     * @param detectedObjects The `StampedDetectedObjects` containing the detected objects 
     *                        and their associated timestamp.
     */
    public CameraProcessed(int time, StampedDetectedObjects detectedObjects) {
        this.processionTime = time; // Set the time when processing is completed.
        this.detectedObjects = detectedObjects; // Store the detected objects.
    }

    /**
     * Retrieves the tick time when the detection is ready for processing or transmission.
     *
     * @return The processing time as an integer.
     */
    public int getProcessionTime() {
        return processionTime;
    }

    /**
     * Retrieves the detected objects associated with this processed detection.
     *
     * @return The `StampedDetectedObjects` containing details about the detection.
     */
    public StampedDetectedObjects getDetectedObject() {
        return detectedObjects;
    }
}