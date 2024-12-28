package bgu.spl.mics.application.messages;

import java.util.List;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.DetectedObject;
import bgu.spl.mics.application.objects.StampedDetectedObjects;

public class DetectObjectsEvent implements Event<Boolean> {
    private final int timeOfSendingEvent;
    private final StampedDetectedObjects detectedObjects;

    public DetectObjectsEvent(StampedDetectedObjects detectedObjects, int time) {
        this.timeOfSendingEvent = time;
        this.detectedObjects = detectedObjects;
    }

    public StampedDetectedObjects getDetectedObjects() {
        return detectedObjects;
    }

    public int getTime() {
        return timeOfSendingEvent;
    }

}
