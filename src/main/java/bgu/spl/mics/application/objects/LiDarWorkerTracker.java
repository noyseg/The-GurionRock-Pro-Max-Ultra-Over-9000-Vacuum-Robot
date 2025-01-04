package bgu.spl.mics.application.objects;

import java.util.LinkedList;
import java.util.List;

/**
 * LiDarWorkerTracker is responsible for managing the processing of LiDAR data.
 * This class is responsible for interacting with the {@link LiDarDataBase} to retrieve
 * cloud point data and generate tracked objects based on detected objects.
 * Each LiDAR worker has an ID, a frequency, and maintains its operational status.
 * It interacts with the simulation components, such as the {@link FusionSlam} service,
 * to send its observations.
 */
public class LiDarWorkerTracker {

    private final int id; // Unique identifier for the LiDAR worker
    private final int frequency; // Frequency at which the LiDAR worker processes events
    private final String name; // Name of the LiDAR worker
    private STATUS status; // Operational status of the LiDAR worker (UP, DOWN, ERROR)
    private List<TrackedObject> lastTrackedObjectList; // List of last tracked objects
    private final LiDarDataBase liDarDataBase; // LiDAR database instance
    private int numOfCameras; // Number of cameras associated with this LiDAR

    /**
     * Constructs a new LiDarWorkerTracker.
     * 
     * @param id           The unique ID of the LiDAR worker.
     * @param frequency    The processing frequency of the LiDAR worker.
     * @param filePath     The file path to the LiDAR database.
     * @param numOfCameras The number of cameras in simulation.
     */
    public LiDarWorkerTracker(int id , int frequency, String filePath, int numOfCameras){
        this.id = id;
        this.frequency = frequency;
        this.name = "LiDar" + String.valueOf(id);
        this.status = STATUS.UP;
        this.lastTrackedObjectList = new LinkedList<>();
        this.liDarDataBase = LiDarDataBase.getInstance(filePath);
        this.numOfCameras = numOfCameras;
    }

    /**
     * @return The unique ID of the LiDAR worker.
     */
    public int getId(){
        return this.id;
    } 

    /**
     * @return The current operational status of the LiDAR worker.
     */
    public STATUS getStatus(){
        return this.status;
    } 

    /**
     * @return The processing frequency of the LiDAR worker.
     */
    public int getFrequency(){
        return this.frequency;
    }

    /**
     * @return The last list of tracked objects processed by this LiDAR worker.
     */
    public List<TrackedObject> getLastTrackedObjectList(){
        return this.lastTrackedObjectList;
    }

    /**
     * @return The instance of the LiDAR database used by this worker.
     */
    public LiDarDataBase getLiDarDataBase(){
        return this.liDarDataBase;
    }

    /**
     * Sets the last list of tracked objects processed by this LiDAR worker.
     * 
     * @param trackedObjects A list of tracked objects to set.
     */
    public void setLastTrackedObjectList(List<TrackedObject> trackedObjects) {
        this.lastTrackedObjectList = trackedObjects;
    } 

    /**
     * Sets the operational status of the LiDAR worker.
     * 
     * @param status The new status of the worker (e.g., UP or DOWN).
     */
    public void setStatus(STATUS status){
        this.status = status;
    } 

    /**
     * @return The name of the LiDAR worker.
     */
    public String getName(){
        return this.name;
    }

    /**
     * @return The number of cameras currently relevant to send data to LiDAR worker 
     */
    public int getCameraCount(){
        return this.numOfCameras;
    }

    /**
     * Decrements the number of cameras associated with this LiDAR worker by 1.
     * 
     * @return The updated number of cameras after decrementing.
     */
    public int decrementCameraCount(){
        return this.numOfCameras -= 1;
    }

    /**
     * Converts a detected object into a tracked object.
     * This involves fetching cloud point data from the database and creating a
     * {@link TrackedObject} using the detected object's data in the database.
     * 
     * @param detectedObject The detected object to process.
     * @param time           The time of detection.
     * @param sender         The name of the sender service.
     * @return A {@link TrackedObject} representing the detected object.
     */
    public TrackedObject detectToTrack(DetectedObject detectedObject, int time, String sender){
        List<List<Double>> cloudPointsData = getLiDarDataBase().getCloudPointsData(time, detectedObject.getId());
        List<CloudPoint> coordinates  = new LinkedList<>();
        for(List<Double> cp :cloudPointsData){
            CloudPoint point = new CloudPoint(cp.get(0), cp.get(1));
            coordinates.add(point);
        }
        return new TrackedObject(time, detectedObject.getId(), detectedObject.getDescription(), coordinates);
    }

    @Override
    public String toString() {
        return "LiDarWorkerTracker{" +
            "id=" + id +
            ", frequency=" + frequency +
            ", name='" + name + '\'' +
            ", status=" + status +
            ", lastTrackedObjectList=" + lastTrackedObjectList +
            ", lidarDataBase=" + (liDarDataBase != null ? "initialized" : "null") +
            '}';
    }
}
