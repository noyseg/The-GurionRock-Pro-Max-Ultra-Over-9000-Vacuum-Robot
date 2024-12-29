package bgu.spl.mics.application.objects;

import java.util.Collections;
import java.util.List;

/**
 * Represents a landmark in the environment map.
 * Landmarks are identified and updated by the FusionSlam service.
 */
public class LandMark {
    private final String id;
    private final String description;
    private final List<CloudPoint> coordinates;

    /**
     * Constructor to initialize the landmark.
     * 
     * @param id          The unique identifier for the landmark
     * @param description A description of the landmark
     * @param coordinates The list of CloudPoints representing the landmark
     */
    public LandMark(String id, String description, List<CloudPoint> coordinates) {
        this.id = id;
        this.description = description;
        this.coordinates = coordinates;
    }

    /**
     * @return The unique identifier for the landmark
     */
    public String getId() {
        return id;
    }

    /**
     * @return The description of the landmark
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return An immutable list of coordinates for the landmark
     */
    public List<CloudPoint> getCoordinates() {
        return coordinates;
    }

    @Override
    public String toString() {
        return "LandMark{" +
                "id='" + id + '\'' +
                ", description='" + description + '\'' +
                ", coordinates=" + coordinates +
                '}';
    }
}

