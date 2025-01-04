package bgu.spl.mics.application.messages;
import bgu.spl.mics.application.objects.*;

import bgu.spl.mics.Event;

public class PoseEvent implements Event<Boolean> {
    private final Pose pose; // The pose associated with this event

    /**
     * Constructs a new PoseEvent with the given pose.
     *
     * @param pose The pose to be associated with the event.
     */
    public PoseEvent(Pose pose) {
        this.pose = pose;
    }

    /**
     * Retrieves the pose associated with this event.
     *
     * @return The pose of the robot at a specific time.
     */
    public Pose getPose() {
        return this.pose;
    }
}
