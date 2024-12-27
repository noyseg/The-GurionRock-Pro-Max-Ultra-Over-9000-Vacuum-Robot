package bgu.spl.mics.application.messages;

import java.util.LinkedList;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.TrackedObject;

public class TrackedObjectsEvent implements Event<TrackedObject>{
    private String senderName;
    private LinkedList<TrackedObject> trackedObject;

    public TrackedObjectsEvent(LinkedList<TrackedObject> trackedObject, String senderName){
        this.senderName = senderName;
        this.trackedObject = trackedObject;
    }

    public String getSenderName(){
        return this.senderName;
    }

    public LinkedList<TrackedObject> getTrackedObject(){
        return this.trackedObject;
    }
}
