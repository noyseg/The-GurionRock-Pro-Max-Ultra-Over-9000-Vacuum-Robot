package bgu.spl.mics.application.messages;

import java.util.LinkedList;
import java.util.List;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.TrackedObject;

public class TrackedObjectsEvent implements Event<Boolean>{
    private String senderName;
    private List<TrackedObject> trackedObjects;

    public TrackedObjectsEvent(List<TrackedObject> trackedObjects, String senderName){
        this.senderName = senderName;
        this.trackedObjects = trackedObjects;
    }

    public String getSenderName(){
        return this.senderName;
    }

    public List<TrackedObject> getTrackedObject(){
        return this.trackedObjects;
    }
}
