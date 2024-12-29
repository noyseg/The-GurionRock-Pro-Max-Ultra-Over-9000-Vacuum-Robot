package bgu.spl.mics.application.objects;
import java.util.LinkedList;
import java.util.List;

public class LidarProcessed {
    private final int processionTime;
    private final List<TrackedObject> trackedObjectsEvents;

    public LidarProcessed(int processionTime, List<TrackedObject> trackedObjectsEvents){
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
