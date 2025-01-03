package bgu.spl.mics.application.objects;

/**
 * Represents an object detected by a camera.
 * It contains information such as the object's ID and description.
 */
public class DetectedObject {

    private final String id;       // Unique identifier for the detected object
    private final String description;    // Description of the detected object

    /**
     * Constructs a new DetectedObject.
     *
     * @param id          The unique identifier for the detected object.
     * @param description A description of the detected object.
     */
    public DetectedObject(String id, String description) {
        this.id = id;
        this.description = description;
    }

    /**
     * @return The unique identifier for the detected object.
     */
    public String getId() {
        return id;
    }

    /**
     * @return The description of the detected object.
     */
    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "DetectedObject{" +
                "id='" + id + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
