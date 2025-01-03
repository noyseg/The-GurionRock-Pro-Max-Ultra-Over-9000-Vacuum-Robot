package bgu.spl.mics.application.services;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

import bgu.spl.mics.MicroService;

import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.*;

/**
 * LiDarService is responsible for processing data from the LiDAR sensor and
 * sending TrackedObjectsEvents to the FusionSLAM service.
 * 
 * This service interacts with the LiDarWorkerTracker object to retrieve and process
 * cloud point data and updates the system's StatisticalFolder upon sending its
 * observations.
 */
public class LiDarService extends MicroService {
    private final LiDarWorkerTracker lidarWorker; // The LidarWorker object associated with this service
    private final PriorityQueue<DetectObjectsEvent> eventsToProcess; // Priority Queue of DetectObjectsEvent waiting to be processed,
    // order by DETECTION time 
    private int currentTick; // Simulation current tick, updated by TickBroadcast 

    /**
     * Constructor for LiDarService.
     *
     * @param LiDarWorkerTracker A LiDAR Tracker worker object that this service will use to process data.
     */
    public LiDarService(LiDarWorkerTracker LiDarWorkerTracker) {
        super(LiDarWorkerTracker.getName());
        this.lidarWorker = LiDarWorkerTracker;
        this.eventsToProcess = new PriorityQueue<>(Comparator.comparingInt(DetectObjectsEvent::getTimeOfDetectedObjects));
        this.currentTick = 0; // Initialized value of simulation
    }

    /**
     * Initializes the LiDarService.
     * Registers the service to handle DetectObjectsEvents and TickBroadcasts,
     * And sets up the necessary callbacks for processing data.
     */
    @Override
    protected void initialize() {
        System.out.println("LiDarService " + getName() + " started");

        // Subscribe to TickBroadcast
        subscribeEvent(DetectObjectsEvent.class, ev -> { 
            if (lidarWorker.getStatus() == STATUS.UP){
                if (ev.getTimeOfDetectedObjects() + lidarWorker.getFrequency() <= currentTick){
                    List<TrackedObject> trackedObjects = new LinkedList<>();
                    processDetectedObjects(ev, trackedObjects);
                    TrackedObjectsEvent tracked = new TrackedObjectsEvent(trackedObjects,getName());
                    sendEvent(tracked);
                    StatisticalFolder.getInstance().incrementTrackedObjects(trackedObjects.size());
                }
                else{
                    eventsToProcess.add(ev);
                }
            }
        });

        // Subscribe to TickBroadcast
        subscribeBroadcast(TickBroadcast.class, tick -> {
            if (lidarWorker.getStatus() == STATUS.UP)
                // Process only if the camera is active
                processTick(tick);
        });

         // Subscribe to TerminatedBroadcast to shuts down when TimeService ends, keep track of camera's counter 
        subscribeBroadcast(TerminatedBroadcast.class, Terminated -> {
            // One of camera shuts down
            if(Terminated.getSenderName().startsWith("C")){
                lidarWorker.decrementCameraCount(); // Decrement CameraCount by one
            }
            if (Terminated.getSenderName().equals("TimeService")) {
                lidarWorker.setStatus(STATUS.DOWN);
                sendBroadcast(new TerminatedBroadcast(getName()));
                terminate();
            }
        });

        // Subscribe to CrashedBroadcast in order to terminate in case of a sensor's crash
        subscribeBroadcast(CrashedBroadcast.class, Crashed -> {
            lidarWorker.setStatus(STATUS.DOWN);
            sendBroadcast(new TerminatedBroadcast(getName())); 
            ErrorCoordinator.getInstance().setLastFramesLidars(getName(),lidarWorker.getLastTrackedObjectList());
            terminate();
        });
    }
    
    /**
     * Processes a single tick broadcast.
     * Updates the current simulation tick, checks for LiDAR errors, and handles 
     * events that are ready for processing.
     * 
     * @param tick The {@link TickBroadcast} containing the current simulation time.
     */
    private void processTick(TickBroadcast tick){
        this.currentTick = tick.getCurrentTime();
        // lidarErrorInTime will return true if there is an error in the relevant lidar data base at tick time
        if (lidarWorker.getLiDarDataBase().lidarErrorInTime(currentTick)){
            handleCrash("LiDar Disconnection");
        }
        else{
            checkTerminationConditionsAndTerminate();
            List<TrackedObject> trackedObjects = new LinkedList<>();
            while (!eventsToProcess.isEmpty() && eventsToProcess.peek().getTimeOfDetectedObjects() + lidarWorker.getFrequency() <= currentTick){
                DetectObjectsEvent dob = eventsToProcess.poll();
                processDetectedObjects(dob , trackedObjects);
            }
            // We can send trackedObject event 
            if (!trackedObjects.isEmpty()){
                TrackedObjectsEvent tracked = new TrackedObjectsEvent(trackedObjects,getName());
                sendEvent(tracked);
                StatisticalFolder.getInstance().incrementTrackedObjects(trackedObjects.size());
            }
        }
    }

    /**
     * Converts a detected objects event into a list of tracked objects and resolves the event.
     * 
     * @param ev The {@link DetectObjectsEvent} containing detected objects.
     * @param trackedObjectsToSend A list where processed tracked objects will be stored.
     */

    // Gets detected DetectObjectsEvent ready right now to be sent, and return correspondent List of TrackedObjects  
    private void processDetectedObjects(DetectObjectsEvent ev, List<TrackedObject> trackedObjectsToSend){
        StampedDetectedObjects stampedDetections = ev.getStampedDetectedObjects(); 
        List<DetectedObject> detectedObjects = stampedDetections.getDetectedObjects(); 
        int time = stampedDetections.getTime();
        for (DetectedObject doe : detectedObjects){
            trackedObjectsToSend.add(lidarWorker.detectToTrack(doe,time,getName()));
        }
        // Set LastTrackedObjectList to contain the newest TrackedObject List
        lidarWorker.setLastTrackedObjectList(trackedObjectsToSend);
        complete(ev, true);
    }

    /**
    * Handles a crash event for the LiDAR service.
    * Logs the error, notifies the error coordinator, and broadcasts a crash event.
    * 
    * @param reason The reason for the crash.
    */
    private void handleCrash(String reason) {
        ErrorCoordinator.getInstance().setLastFramesLidars(getName(), lidarWorker.getLastTrackedObjectList());
        ErrorCoordinator.getInstance().setCrashed(getName(), currentTick, reason);
        sendBroadcast(new CrashedBroadcast(getName()));
        terminate();
    }

    /**
     * Checks termination conditions for the LiDAR service.
     * Terminates the service if there are no remaining events or active cameras.
     */
    private void checkTerminationConditionsAndTerminate() {
        if (eventsToProcess.isEmpty() && lidarWorker.getCameraCount() == 0) {
            sendBroadcast(new TerminatedBroadcast(getName()));
            terminate();
        }
    }
}
