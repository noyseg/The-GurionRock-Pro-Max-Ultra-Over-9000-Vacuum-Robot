package bgu.spl.mics.application.objects;
import java.util.LinkedList;

public class LidarProcessed {
    private final int processionTime;
    private final LinkedList<TrackedObject> trackedObjectsEvents;

    public LidarProcessed(int processionTime, LinkedList<TrackedObject> trackedObjectsEvents){
        this.processionTime = processionTime;
        this.trackedObjectsEvents = trackedObjectsEvents;
    }

    // Returns Procession Time for current trackedObjectsEvents
    public int getProcessionTime(){
        return processionTime;
    }

    public LinkedList<TrackedObject> getTrackedObjectsEvents(){
        return this.getTrackedObjectsEvents();
    }


}
