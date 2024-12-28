package bgu.spl.mics.application.messages;

import java.util.LinkedList;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.TrackedObject;

public class TrackedObjectsEvent implements Event<Boolean>{
    private String senderName;
    private LinkedList<TrackedObject> trackedObjects;

    public TrackedObjectsEvent(LinkedList<TrackedObject> trackedObjects, String senderName){
        this.senderName = senderName;
        this.trackedObjects = trackedObjects;
    }

    public String getSenderName(){
        return this.senderName;
    }

    public LinkedList<TrackedObject> getTrackedObject(){
        return this.trackedObjects;
    }
}
