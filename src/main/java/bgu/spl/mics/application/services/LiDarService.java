package bgu.spl.mics.application.services;

import java.util.LinkedList;

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
    private final LinkedList<LidarProcessed> lpList;

    /**
     * Constructor for LiDarService.
     *
     * @param LiDarWorkerTracker A LiDAR Tracker worker object that this service will use to process data.
     */
    public LiDarService(LiDarWorkerTracker LiDarWorkerTracker) {
        super("Lidar" + LiDarWorkerTracker.getId());
        this.lidarWorker = LiDarWorkerTracker;
        this.lpList = new LinkedList<LidarProcessed>();
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
        if(lpList.getFirst() != null && lpList.getFirst().getProcessionTime() == tick.getCurrentTime()){
            sendEvent(new TrackedObjectsEvent(lpList.getFirst().getTrackedObjectsEvents(),getName())); // Sends event to fusion slum
            lpList.remove(lpList.getFirst());
        }
        if (LiDarDataBase.getInstance(getName()) == tick.getCurrentTime()) {
            
        }
        LidarProcessed lp = new LidarProcessed(tick.getCurrentTime()+lidarWorker.getFrequency(),LiDarDataBase.getInstance())
        lpList.addLast(lp));
    }
}
