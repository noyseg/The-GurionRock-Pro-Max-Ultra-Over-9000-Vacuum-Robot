package bgu.spl.mics.application.objects;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

import bgu.spl.mics.application.messages.DetectObjectsEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.TrackedObjectsEvent;

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
    private final PriorityQueue<DetectObjectsEvent> eventsToProcess; // Priority Queue of DetectObjectsEvent waiting to be processed,
    // order by DETECTION time 
    private int currentTick; // Simulation current tick, updated by TickBroadcast 

    /**
     * Constructs a new LiDarWorkerTracker.
     *
     * @param id           The unique ID of the LiDAR worker.
     * @param frequency    The processing frequency of the LiDAR worker.
     * @param filePath     The file path to the LiDAR database.
     * @param numOfCameras The number of cameras in simulation.
     */
    public LiDarWorkerTracker(int id, int frequency, String filePath, int numOfCameras) {
        this.id = id;
        this.frequency = frequency;
        this.name = "LiDarWorkerTracker" + String.valueOf(id);
        this.status = STATUS.UP;
        this.lastTrackedObjectList = new LinkedList<>();
        this.liDarDataBase = LiDarDataBase.getInstance(filePath);
        this.numOfCameras = numOfCameras;
        this.eventsToProcess = new PriorityQueue<>(Comparator.comparingInt(DetectObjectsEvent::getTimeOfDetectedObjects));
        this.currentTick = 0; // Initialized value of simulation
    }

    /**
     * @return The unique ID of the LiDAR worker.
     */
    public int getId() {
        return this.id;
    }

    /**
     * @return The current operational status of the LiDAR worker.
     */
    public STATUS getStatus() {
        return this.status;
    }

    /**
     * @return The processing frequency of the LiDAR worker.
     */
    public int getFrequency() {
        return this.frequency;
    }

    /**
     * @return The last list of tracked objects processed by this LiDAR worker.
     */
    public List<TrackedObject> getLastTrackedObjectList() {
        return this.lastTrackedObjectList;
    }

    /**
     * @return The instance of the LiDAR database used by this worker.
     */
    public LiDarDataBase getLiDarDataBase() {
        return this.liDarDataBase;
    }
    /**
     * This function only used for testing.
     * @return The DetectObjectsEvent to be processed.
     */
    public PriorityQueue<DetectObjectsEvent> getEventToProcess() {
        return this.eventsToProcess;
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
    public void setStatus(STATUS status) {
        this.status = status;
    }

    /**
     * @return The name of the LiDAR worker.
     */
    public String getName() {
        return this.name;
    }

    /**
     * @return The number of cameras currently relevant to send data to LiDAR worker
     */
    public int getCameraCount() {
        return this.numOfCameras;
    }

    /**
     * Decrements the number of cameras associated with this LiDAR worker by 1.
     *
     * @return The updated number of cameras after decrementing.
     */
    public int decrementCameraCount() {
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
     * @pre detectedObject != null
     * @pre time >= 0
     * @pre sender != null
     * @post A new {@link TrackedObject} is created with the provided detection data.
     * @post The {@link TrackedObject} contains all cloud points associated with the detected object.
     * @inv The {@link DetectedObject} and its associated cloud point data remain unaltered.
     */
    public TrackedObject detectToTrack(DetectedObject detectedObject, int time, String sender) {
        List<List<Double>> cloudPointsData = getLiDarDataBase().getCloudPointsData(time, detectedObject.getId());
        List<CloudPoint> coordinates = new LinkedList<>();
        for (List<Double> cp : cloudPointsData) {
            CloudPoint point = new CloudPoint(cp.get(0), cp.get(1));
            coordinates.add(point);
        }
        return new TrackedObject(time, detectedObject.getId(), detectedObject.getDescription(), coordinates);
    }

    /**
     * Processes a single tick broadcast.
     * Updates the current simulation tick, checks for LiDAR errors, and processes
     * detection events if their time is ready. Generates a {@link TrackedObjectsEvent}
     * if tracked objects are available to send.
     *
     * @param tick The {@link TickBroadcast} containing the current simulation time.
     * @return A {@link TrackedObjectsEvent} containing processed tracked objects, or null if none are ready or error occurred.
     * @pre tick != null
     * @pre tick.getCurrentTime() > 0
     * @post If there are {@link TrackedObject} ready to be sent at current tick {@link lastTrackObjectList} is updated to be 
     * the list of ones that are ready to be sent and {@link StatisticalFolder} will be updated
     * @post {@link eventToProcess} will contain only {@link TrackedObject} with time of detection + {@link frequency} < tick
     * @post If an error occurs in tick time at the liDarDataBase and {@link status} will be ERROR and {@link ErrorCoordinator} will be updated
     * @post If there are no events to process and no cameras are active and {@link status} will be DOWN
     * @inv The {@link LiDarDataBase} remains unaltered.
     */
    public TrackedObjectsEvent processTick(TickBroadcast tick) {
        this.currentTick = tick.getCurrentTime();
        // lidarErrorInTime will return true if there is an error in the relevant lidar data base at tick time
        if (getLiDarDataBase().lidarErrorInTime(currentTick)) {
            this.setStatus(STATUS.ERROR);
            ErrorCoordinator.getInstance().setLastFramesLidars(getName(), getLastTrackedObjectList());
            ErrorCoordinator.getInstance().setCrashed("LiDarWorkerTracker " + String.valueOf(this.id), currentTick, "LiDar Disconnection");
            return null;
        }
        if (eventsToProcess.isEmpty() && this.numOfCameras == 0) {
            setStatus(STATUS.DOWN);
        } else {
            List<TrackedObject> trackedObjects = new LinkedList<>();
            while (!eventsToProcess.isEmpty() && eventsToProcess.peek().getTimeOfDetectedObjects() + this.frequency <= currentTick) {
                DetectObjectsEvent dob = eventsToProcess.poll();
                processDetectedObjects(dob, trackedObjects);
            }
            // We can send trackedObject event 
            if (!trackedObjects.isEmpty()) {
                StatisticalFolder.getInstance().incrementTrackedObjects(trackedObjects.size());
                return new TrackedObjectsEvent(trackedObjects, getName());
            }
        }
        return null;
    }

    /**
     * Processes a detected objects event and resolves it into tracked objects.
     * Adds the detected objects to a list of tracked objects for further processing.
     *
     * @param ev                   The {@link DetectObjectsEvent} to process.
     * @param trackedObjectsToSend A list to store the processed tracked objects.
     */
    private void processDetectedObjects(DetectObjectsEvent ev, List<TrackedObject> trackedObjectsToSend) {
        StampedDetectedObjects stampedDetections = ev.getStampedDetectedObjects();
        List<DetectedObject> detectedObjects = stampedDetections.getDetectedObjects();
        int time = stampedDetections.getTime();
        for (DetectedObject doe : detectedObjects) {
            trackedObjectsToSend.add(detectToTrack(doe, time, getName()));
        }
        // Set LastTrackedObjectList to contain the newest TrackedObject List
        setLastTrackedObjectList(trackedObjectsToSend);
    }

    /**
     * Processes a detection event immediately if ready, or adds it to the queue otherwise.
     *
     * @param ev The {@link DetectObjectsEvent} to process.
     * @return A {@link TrackedObjectsEvent} if processed immediately, or null if added to the queue.
     */
    public TrackedObjectsEvent processDetectedEvent(DetectObjectsEvent ev) {
        if (ev.getTimeOfDetectedObjects() + frequency <= currentTick) {
            List<TrackedObject> trackedObjects = new LinkedList<>();
            processDetectedObjects(ev, trackedObjects);
            StatisticalFolder.getInstance().incrementTrackedObjects(trackedObjects.size());
            return new TrackedObjectsEvent(trackedObjects, getName());
        } else {
            eventsToProcess.add(ev);
        }
        return null;
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
