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

    public LiDarDataBase getLiDarDataBase(){
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

    public TrackedObject detectToTrack(DetectedObject detectedObject, int time){
        //System.out.println(detectedObject);
        //ObjectDataTracker objData = new ObjectDataTracker(detectedObject.getID(),time);
        //List<List<Double>> cloudPointsData = getLiDarDataBase().getstampedCloudPointsMap().get(objData); // Gets relevent cloud Points Data from lidar Date Base 
        List<List<Double>> cloudPointsData = getLiDarDataBase().getCloudPointsData(time,detectedObject.getID());
        List<CloudPoint> coordinates  = new LinkedList<>();
        //System.out.println(cloudPointsData);
        // Transform List<List<Double>> to list of CloudPoint
        for(List<Double> cp :cloudPointsData){
            CloudPoint point = new CloudPoint(cp.get(0), cp.get(1));
            coordinates.add(point);
        }
        return new TrackedObject(time, detectedObject.getID(), detectedObject.getDescription(), coordinates);
    }

    @Override
    public String toString() {
        return "LiDarWorkerTracker{" +
            "id=" + id +
            ", frequency=" + frequency +
            ", name='" + name + '\'' +
            ", status=" + status +
            ", lastTrackedObjectList=" + lastTrackedObjectList +
            ", lidarDataBase=" + (lbd != null ? "initialized" : "null") +
            '}';
    }

}
