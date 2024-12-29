package bgu.spl.mics.application.messages;

import java.util.List;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.DetectedObject;
import bgu.spl.mics.application.objects.StampedDetectedObjects;

public class DetectObjectsEvent implements Event<Boolean> {
    private final int timeOfDetectedObjects;
    private final StampedDetectedObjects detectedObjects;
    private String senderName;

    public DetectObjectsEvent(StampedDetectedObjects detectedObjects, int time,String name) {
        this.timeOfDetectedObjects = time;
        this.detectedObjects = detectedObjects;
        this.senderName = name;
    }

    public StampedDetectedObjects getStampedDetectedObjects() {
        return detectedObjects;
    }

    public int getTimeOfDetectedObjects() {
        return timeOfDetectedObjects;
    }

    public String getSenderName() {
        return senderName;
    }

}
