package bgu.spl.mics.application.messages;

import java.util.List;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.TrackedObject;

public class TrackedObjectsEvent implements Event<Boolean>{
    private String senderName; // The name of the sender of the event
    private List<TrackedObject> trackedObjects; // The list of tracked objects associated with the event

    /**
     * Constructs a new TrackedObjectsEvent.
     *
     * @param trackedObjects The list of tracked objects to be associated with the event.
     * @param senderName     The name of the sender initiating the event.
     */
    public TrackedObjectsEvent(List<TrackedObject> trackedObjects, String senderName) {
        this.senderName = senderName;
        this.trackedObjects = trackedObjects;
    }

    /**
     * Retrieves the name of the sender of the event.
     *
     * @return The name of the sender.
     */
    public String getSenderName() {
        return this.senderName;
    }

    /**
     * Retrieves the list of tracked objects associated with the event.
     *
     * @return The list of tracked objects.
     */
    public List<TrackedObject> getTrackedObjects() {
        return this.trackedObjects;
    }
}
