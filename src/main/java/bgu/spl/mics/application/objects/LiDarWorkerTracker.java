package bgu.spl.mics.application.objects;

import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * LiDarWorkerTracker is responsible for managing a LiDAR worker.
 * It processes DetectObjectsEvents and generates TrackedObjectsEvents by using data from the LiDarDataBase.
 * Each worker tracks objects and sends observations to the FusionSlam service.
 */
public class LiDarWorkerTracker {

    private final int id;
    private final int frequency;
    private final String name;
    private STATUS status;
    private List<TrackedObject> lastTrackedObjectList;
    private LiDarDataBase lbd;

    public LiDarWorkerTracker(int id , int frequency, String filePath){
        this.id = id;
        this.frequency = frequency;
        this.name = "Lidar";
        this.status = STATUS.UP;
        this.lastTrackedObjectList = new LinkedList<TrackedObject>();
        this.lbd = LiDarDataBase.getInstance(filePath);
    }

    public int getId(){
        return this.id;
    } 

    public STATUS getStatus(){
        return this.status;
    } 

    public int getFrequency(){
        return this.frequency;
    }

    public List<TrackedObject> getLastTrackedObjectList(){
        return this.lastTrackedObjectList;
    }

    public LiDarDataBase getlLiDarDataBase(){
        return this.lbd;
    }

    public void setLastTrackedObjectList(List<TrackedObject> trackedObjects) {
        this.lastTrackedObjectList = trackedObjects;
    } 

    public void setStatus(STATUS status){
        this.status = status;
    } 

    public String getName(){
        return this.name;
    }

   
}
