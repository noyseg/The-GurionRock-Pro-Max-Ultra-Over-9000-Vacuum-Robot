package bgu.spl.mics.application.services;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

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
    private final PriorityQueue<DetectObjectsEvent> eventsToProcess;
    private List<TrackedObject> trackedObjects;

    /**
     * Constructor for LiDarService.
     *
     * @param LiDarWorkerTracker A LiDAR Tracker worker object that this service will use to process data.
     */
    public LiDarService(LiDarWorkerTracker LiDarWorkerTracker) {
        super("Lidar" + LiDarWorkerTracker.getId());
        this.lidarWorker = LiDarWorkerTracker;
        this.eventsToProcess = new PriorityQueue<>(Comparator.comparingInt(DetectObjectsEvent::getTimeOfDetectedObjects);
        this.trackedObjects = new LinkedList<>();
    }

    /**
     * Initializes the LiDarService.
     * Registers the service to handle DetectObjectsEvents and TickBroadcasts,
     * and sets up the necessary callbacks for processing data.
     */
    @Override
    protected void initialize() {
        System.out.println("LiDarService " + getName() + " started");

        /// What should be first?? tick or event
        subscribeEvent(DetectObjectsEvent.class, ev -> { 
            if (lidarWorker.getStatus() == STATUS.UP){
                if (ev.getTimeOfDetectedObjects() + lidarWorker.getFrequency() > )
            }
        });

        subscribeBroadcast(TickBroadcast.class, tick -> {
            if (lidarWorker.getStatus() == STATUS.UP)
                processTick(tick);
        });
        subscribeBroadcast(TerminatedBroadcast.class, Terminated -> {
        
        });
        subscribeBroadcast(TerminatedBroadcast.class, Crashed -> {
            lidarWorker.setStatus(STATUS.DOWN);
            sendBroadcast(new TerminatedBroadcast(getName())); // Sending error of last tracked
        });
        // Subscribes to TickBroadcast, TerminatedBroadcast, CrashedBroadcast, DetectObjectsEvent.
    }

    
    private void processTick(TickBroadcast tick){
        List<TrackedObject> trackedObjectsToFusion = new LinkedList<>();
        // As long as we have detected objects that can be send at this moment
        while (!eventsToProcess.isEmpty() && eventsToProcess.peek().getTimeOfDetectedObjects() + lidarWorker.getFrequency() <= tick.getCurrentTime()){
            DetectObjectsEvent dob = eventsToProcess.poll();
            processDetectedObjects(dob);
        }
        if (!trackedObjects.isEmpty()){
            TrackedObjectsEvent tracked = new TrackedObjectsEvent(trackedObjects,getName()); // Sends event to fusion slum
            StatisticalFolder.getInstance().incrementTrackedObjects(trackedObjects.size());
            lidarWorker.setLastTrackedObjectList(trackedObjects);
            trackedObjects = new LinkedList<>();
            Future<Boolean> future = (Future<Boolean>) sendEvent(tracked);
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

    private void processDetectedObjects(DetectObjectsEvent ev){
        StampedDetectedObjects toProcess = ev.getStampedDetectedObjects();
        List<DetectedObject> detectedObjects = toProcess.getDetectedObjects();
        int time = toProcess.getTime();
        for (DetectedObject doe : detectedObjects){
            ObjectDataTracker objData = new ObjectDataTracker(doe.getID(),time);
            List<List<Double>> cloupPointsData = lidarWorker.getlLiDarDataBase().getstampedCloudPointsMap().get(objData);
            List<CloudPoint> coordinates  = new LinkedList<>();
            for(List<Double> cp :cloupPointsData){
                CloudPoint point = new CloudPoint(cp.get(0), cp.get(1));
                coordinates.add(point);
            }
            TrackedObject trackedO = new TrackedObject(time, doe.getID(), doe.getDescription(), coordinates);
            trackedObjects.add(trackedO);
        }
        complete(ev, true);
    }


    // private void processDetectedObjects(DetectObjectsEvent ev){
    //     StampedDetectedObjects toProcess = ev.getStampedDetectedObjects();
    //     List<DetectedObject> detectedObjects = toProcess.getDetectedObjects();
    //     int time = toProcess.getTime();
    //     for (DetectedObject doe : detectedObjects){
    //         ObjectDataTracker objData = new ObjectDataTracker(doe.getID(),time);
    //         List<List<Double>> cloupPointsData = lidarWorker.getlLiDarDataBase().getstampedCloudPointsMap().get(objData);
    //         List<CloudPoint> coordinates  = new LinkedList<>();
    //         int i = 0;
    //         for(List<Double> cp :cloupPointsData){
    //             CloudPoint point = new CloudPoint(cp.get(0), cp.get(1));
    //             coordinates.add(point);
    //         }
    //         TrackedObject trackedO = new TrackedObject(time, doe.getID(), doe.getDescription(), coordinates);
    //         lidarProccessedList.add(trackedO);
    //     }
    //     complete(ev, true);
    // }ג
    
    // private void processTick(TickBroadcast tick){
    //     List<TrackedObject> trackedObjectsToFusion = new LinkedList<>();
    //     while (!lidarProccessedList.isEmpty() && lidarProccessedList.peek().getTime() <= tick.getCurrentTime()) {
    //         trackedObjectsToFusion.add(lidarProccessedList.poll());
    //     }
    //     if (!trackedObjectsToFusion.isEmpty()){
    //         TrackedObjectsEvent tracked = new TrackedObjectsEvent(trackedObjectsToFusion,getName()); // Sends event to fusion slum
    //         StatisticalFolder.getInstance().incrementTrackedObjects(trackedObjectsToFusion.size());
    //         lidarWorker.setLastTrackedObjectList(trackedObjectsToFusion);
    //         Future<Boolean> future = (Future<Boolean>) sendEvent(tracked);
    //         try {
    //             if (future.get() == false) {
    //                 System.out.println("Fusion Slum could not handle the tracked objects");
    //                 sendBroadcast(new TerminatedBroadcast(getName()));
    //                 terminate();
    //             }
    //         } catch (Exception e) {
    //             e.printStackTrace();
    //             sendBroadcast(new CrashedBroadcast(getName()));
    //         }
    //     }
    // }

    // private void processDetectedObjects(DetectObjectsEvent ev){
    //     StampedDetectedObjects toProcess = ev.getStampedDetectedObjects();
    //     List<DetectedObject> detectedObjects = toProcess.getDetectedObjects();
    //     int time = toProcess.getTime();
    //     for (DetectedObject doe : detectedObjects){
    //         ObjectDataTracker objData = new ObjectDataTracker(doe.getID(),time);
    //         List<List<Double>> cloupPointsData = lidarWorker.getlLiDarDataBase().getstampedCloudPointsMap().get(objData);
    //         List<CloudPoint> coordinates  = new LinkedList<>();
    //         int i = 0;
    //         for(List<Double> cp :cloupPointsData){
    //             CloudPoint point = new CloudPoint(cp.get(0), cp.get(1));
    //             coordinates.add(point);
    //         }
    //         TrackedObject trackedO = new TrackedObject(time, doe.getID(), doe.getDescription(), coordinates);
    //         lidarProccessedList.add(trackedO);
    //     }
    //     complete(ev, true);
    // }
}
