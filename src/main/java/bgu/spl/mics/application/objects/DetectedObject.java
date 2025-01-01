package bgu.spl.mics.application.objects;

/**
 * DetectedObject represents an object detected by the camera.
 * It contains information such as the object's ID and description.
 */
public class DetectedObject {
    private final String id;
    private String description;

    public DetectedObject(String id,String description) {
        this.id =id;
        this.description = description;
    }

    public String getID(){
        return this.id;
    }
    public String getDescription(){
        return this.description;
    }

    @Override
    public String toString() {
        return "DetectedObject{" +
            "id='" + id + '\'' +
            ", description='" + description + '\'' +
            '}';
    }
}
