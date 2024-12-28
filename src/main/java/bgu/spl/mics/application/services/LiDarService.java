package bgu.spl.mics.application.services;

import java.util.LinkedList;

import bgu.spl.mics.Future;
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
    private final LiDarWorkerTracker lidarWorker;
    private final LinkedList<LidarProcessed> lidarProccessedList;

    /**
     * Constructor for LiDarService.
     *
     * @param LiDarWorkerTracker A LiDAR Tracker worker object that this service will use to process data.
     */
    public LiDarService(LiDarWorkerTracker LiDarWorkerTracker) {
        super("Lidar" + LiDarWorkerTracker.getId());
        this.lidarWorker = LiDarWorkerTracker;
        this.lidarProccessedList = new LinkedList<LidarProcessed>();
    }

    /**
     * Initializes the LiDarService.
     * Registers the service to handle DetectObjectsEvents and TickBroadcasts,
     * and sets up the necessary callbacks for processing data.
     */
    @Override
    protected void initialize() {
        System.out.println("LiDarService " + getName() + " started");
        subscribeEvent(DetectObjectsEvent.class, ev -> { 

        });

        subscribeBroadcast(TickBroadcast.class, tick -> {
            if (lidarWorker.getStatus() == STATUS.UP)
            processTick(tick);
        });

        subscribeBroadcast(TerminatedBroadcast.class, Terminated -> {

        }
        subscribeBroadcast(TerminatedBroadcast.class, Crashed -> {

        }
        // Subscribes to TickBroadcast, TerminatedBroadcast, CrashedBroadcast, DetectObjectsEvent.
    }

    private void processTickDetectObjects(ev){

    }
    

    private void processTick(TickBroadcast tick){
        if(lidarProccessedList.getFirst() != null && lidarProccessedList.getFirst().getProcessionTime() >= tick.getCurrentTime()){
            TrackedObjectsEvent tracked = new TrackedObjectsEvent(lidarProccessedList.getFirst().getTrackedObjectsEvents(),getName()); // Sends event to fusion slum
            Future<Boolean> future = (Future<Boolean>) sendEvent(tracked);
            lidarProccessedList.remove(lidarProccessedList.getFirst());
            try {
                if (future.get() == false) {
                    System.out.println("Fusion Slum could not handle the tracked objects");
                    sendBroadcast(new TerminatedBroadcast(getName()));
                    terminate();
                }
            } catch (Exception e) {
                e.printStackTrace();
                sendBroadcast(new CrashedBroadcast(getName()));
            }
        }
    }
}
