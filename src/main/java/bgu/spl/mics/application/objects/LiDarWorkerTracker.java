package bgu.spl.mics.application.objects;

import java.io.ObjectInputFilter.Status;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

/**
 * LiDarWorkerTracker is responsible for managing a LiDAR worker.
 * It processes DetectObjectsEvents and generates TrackedObjectsEvents by using data from the LiDarDataBase.
 * Each worker tracks objects and sends observations to the FusionSlam service.
 */
public class LiDarWorkerTracker {

    private final int id;
    private final int frequency;
    private STATUS status;
    private List<TrackedObject> lastTrackedObjectList;
    private int numTrackedObjects; 

    public LiDarWorkerTracker(int id , int frequency){
        this.id = id;
        this.frequency = frequency;
        this.status = STATUS.UP;
        this.lastTrackedObjectList = new ArrayList<TrackedObject>();
        this.numTrackedObjects = 0;
    }

    public int getId(){
        return this.id;
    } 

    public int getNumTrackedObjects(){
        return numTrackedObjects;
    } 

    public void setNumTrackedObjects(int newTickDetection){
        numTrackedObjects += newTickDetection;
    }

    public int getFrequency(){
        return this.frequency;
    }

    public List<TrackedObject> getLastTrackedObjectList(){
        return this.lastTrackedObjectList;
    }
   
}
