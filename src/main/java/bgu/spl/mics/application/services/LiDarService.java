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
     * and sets up the necessary callbacks for processing data.
     */
    @Override
    protected void initialize() {
        System.out.println("LiDarService " + getName() + " started");

        // Handle DetectObjectsEvent
        subscribeEvent(DetectObjectsEvent.class, ev -> { 
            if (lidarWorker.getStatus() == STATUS.UP){
                // 
                if (ev.getTimeOfDetectedObjects() + lidarWorker.getFrequency() <= currentTick){
                    List<TrackedObject> trackedObjects = new LinkedList<>();
                    processDetectedObjects(ev, trackedObjects);
                    TrackedObjectsEvent tracked = new TrackedObjectsEvent(trackedObjects,getName());
                    sendEvent(tracked);
                }
                else{
                    eventsToProcess.add(ev);
                }
            }
        });

        // Handle TickBroadcast
        subscribeBroadcast(TickBroadcast.class, tick -> {
            if (lidarWorker.getStatus() == STATUS.UP)
                processTick(tick);
        });

        subscribeBroadcast(TerminatedBroadcast.class, Terminated -> {
            if(Terminated.getSenderName().startsWith("c")){
                lidarWorker.setCameraCount();
            }
            if (Terminated.getSenderName().equals("TimeService")) {
                lidarWorker.setStatus(STATUS.DOWN);
                sendBroadcast(new TerminatedBroadcast(getName()));
                terminate();
            }
        });

        subscribeBroadcast(CrashedBroadcast.class, Crashed -> {
            System.out.println("Crashed");
            lidarWorker.setStatus(STATUS.DOWN);
            ErrorCoordinator.getInstance().setLastFramesLidars(getName(),lidarWorker.getLastTrackedObjectList());;
            sendBroadcast(new TerminatedBroadcast(getName())); 
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
        // lidarErrorInTime will return true if there is an error
        if (lidarWorker.getLiDarDataBase().lidarErrorInTime(currentTick)){
            handleCrash("LiDar Disconnection");
        }
        else{
            checkTerminationConditionsAndTerminate();
            // As long as we have detected objects that can be send at this moment
            List<TrackedObject> trackedObjects = new LinkedList<>();
            while (!eventsToProcess.isEmpty() && eventsToProcess.peek().getTimeOfDetectedObjects() + lidarWorker.getFrequency() <= currentTick){
                DetectObjectsEvent dob = eventsToProcess.poll();
                processDetectedObjects(dob , trackedObjects);
            }
            if (!trackedObjects.isEmpty()){
                TrackedObjectsEvent tracked = new TrackedObjectsEvent(trackedObjects,getName()); // Sends event to fusion slum
                sendEvent(tracked);
            }
        }
    }

    /**
     * Converts a detected objects event into a list of tracked objects and resolves the event.
     * 
     * @param ev The {@link DetectObjectsEvent} containing detected objects.
     * @return A list of {@link TrackedObject} that represents the detected objects.
     */

    // Gets detected DetectObjectsEvent ready right now to be sent, and return correspondent List of TrackedObjects  
    private void processDetectedObjects(DetectObjectsEvent ev, List<TrackedObject> trackedObjectsToSend){
        StampedDetectedObjects toProcess = ev.getStampedDetectedObjects(); // Extract the StampedDetectedObjects themselves
        List<DetectedObject> detectedObjects = toProcess.getDetectedObjects();  // Extract the DetectedObjects themselves
        int time = toProcess.getTime();
        for (DetectedObject doe : detectedObjects){
            trackedObjectsToSend.add(lidarWorker.detectToTrack(doe,time,getName()));
        }
        lidarWorker.setLastTrackedObjectList(trackedObjectsToSend);
        StatisticalFolder.getInstance().incrementTrackedObjects(trackedObjectsToSend.size());
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
