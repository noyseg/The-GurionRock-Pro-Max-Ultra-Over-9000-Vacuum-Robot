package bgu.spl.mics.application.services;

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

    /**
     * Constructor for LiDarService.
     *
     * @param LiDarWorkerTracker A LiDAR Tracker worker object that this service will use to process data.
     */
    public LiDarService(LiDarWorkerTracker LiDarWorkerTracker) {
        super(LiDarWorkerTracker.getName());
        this.lidarWorker = LiDarWorkerTracker;
    }

    /**
     * Initializes the LiDarService.
     * Registers the service to handle DetectObjectsEvents and TickBroadcasts,
     * And sets up the necessary callbacks for processing data.
     */
    @Override
    protected void initialize() {

        // Subscribe to TickBroadcast
        subscribeEvent(DetectObjectsEvent.class, ev -> { 
            if (lidarWorker.getStatus() == STATUS.UP){
                TrackedObjectsEvent tracked = lidarWorker.processDetectedEvent(ev);
                if (tracked != null)
                    sendEvent(tracked);
            }
        });

        // Subscribe to TickBroadcast
        subscribeBroadcast(TickBroadcast.class, tick -> {
            if (lidarWorker.getStatus() == STATUS.UP){
                // Process only if the camera is active
                TrackedObjectsEvent toe = lidarWorker.processTick(tick);
                if (toe != null){
                    sendEvent(toe); 
                }
            }
            if (lidarWorker.getStatus() == STATUS.ERROR){
                sendBroadcast(new CrashedBroadcast(getName()));
                terminate();
            }
            if (lidarWorker.getStatus() == STATUS.DOWN){
                sendBroadcast(new TerminatedBroadcast(getName()));
                terminate();
            }
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
}
