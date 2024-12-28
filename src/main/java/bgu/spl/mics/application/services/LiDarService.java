package bgu.spl.mics.application.services;

import java.util.LinkedList;
import java.util.List;
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
            if (lidarWorker.getStatus() == STATUS.UP){
                processDetectedObjects(ev);
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
        if(lidarProccessedList.getFirst() != null && lidarProccessedList.getFirst().getProcessionTime() <= tick.getCurrentTime()){
            List<TrackedObject> trackedObjects = lidarProccessedList.getFirst().getTrackedObjectsEvents();
            TrackedObjectsEvent tracked = new TrackedObjectsEvent(trackedObjects,getName()); // Sends event to fusion slum
            StatisticalFolder.getInstance().addTrackedObjects(trackedObjects.size());
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

    private void processDetectedObjects(DetectObjectsEvent ev){
        StampedDetectedObjects toProcess = ev.getDetectedObjects();
        List<DetectedObject> detectedObjects = toProcess.getDetectedObjects();
        List<TrackedObject> trackedObjects = new LinkedList<>();
        int time = toProcess.getTime();
        for (DetectedObject doe : detectedObjects){
            objectDataTracker objData = new objectDataTracker(doe.getID(),time);
            List<List<Double>> cloupPointsData = lidarWorker.getlLiDarDataBase().getstampedCloudPointsMap().get(objData);
            CloudPoint[] coordinates  = new CloudPoint[cloupPointsData.size()];
            int i = 0;
            for(List<Double> cp :cloupPointsData){
                CloudPoint point = new CloudPoint(cp.get(0), cp.get(1));
                coordinates[i] = point;
                i++; 
            }
            TrackedObject trackedO = new TrackedObject(time, doe.getID(), doe.getDescription(), coordinates);
            trackedObjects.add(trackedO);
        }
        lidarWorker.setLastTrackedObjectList(trackedObjects); // if this is what they ment 
        LidarProcessed lp = new LidarProcessed(toProcess.getTime() + lidarWorker.getFrequency(),trackedObjects);
        lidarProccessedList.add(lp);
        complete(ev, true);
    }
}
