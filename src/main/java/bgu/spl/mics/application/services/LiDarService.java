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
    private int currentTick;

    /**
     * Constructor for LiDarService.
     *
     * @param LiDarWorkerTracker A LiDAR Tracker worker object that this service will use to process data.
     */
    public LiDarService(LiDarWorkerTracker LiDarWorkerTracker) {
        super("Lidar" + LiDarWorkerTracker.getId());
        this.lidarWorker = LiDarWorkerTracker;
        this.eventsToProcess = new PriorityQueue<>(Comparator.comparingInt(DetectObjectsEvent::getTimeOfDetectedObjects));
        this.currentTick = 0;
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
            System.err.println("CAMARA DETECT " +  ev.getStampedDetectedObjects().getDetectedObjects().size());
            if (lidarWorker.getStatus() == STATUS.UP){
                if (ev.getTimeOfDetectedObjects() + lidarWorker.getFrequency() <= currentTick){
                    List<TrackedObject> processToBeSent = processDetectedObjects(ev);
                    StatisticalFolder.getInstance().incrementTrackedObjects(processToBeSent.size());
                    TrackedObjectsEvent tracked = new TrackedObjectsEvent(processToBeSent,getName());
                    Future<Boolean> future = (Future<Boolean>) sendEvent(tracked);
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
            if (Terminated.getSenderName().equals("TimeService")) {
                lidarWorker.setStatus(STATUS.DOWN);
                sendBroadcast(new TerminatedBroadcast(getName()));
                terminate();
            }
        });

        subscribeBroadcast(CrashedBroadcast.class, Crashed -> {
            System.out.println("Crashed");
            lidarWorker.setStatus(STATUS.DOWN);
            //LastFrameLidar lf = new LastFrameLidar(getName(), lidarWorker.getLastTrackedObjectList());
            ErrorCoordinator.getInstance().setLastFramesLidars(getName(),lidarWorker.getLastTrackedObjectList());;
            sendBroadcast(new TerminatedBroadcast(getName())); 
            terminate();
        });
        // Subscribes to TickBroadcast, TerminatedBroadcast, CrashedBroadcast, DetectObjectsEvent.
    }
    
    private void processTick(TickBroadcast tick){
        this.currentTick = tick.getCurrentTime();
        // lidarErrorInTime will return true if there is an error
        if (lidarWorker.getLiDarDataBase().lidarErrorInTime(currentTick)){
            ErrorCoordinator.getInstance().setLastFramesLidars(getName(),lidarWorker.getLastTrackedObjectList());
            ErrorCoordinator.getInstance().setCrashed(getName(), currentTick, "Lidar disconnected");
            sendBroadcast(new CrashedBroadcast(getName()));
            terminate();
        }
        else{
            // Lidar need to be finished - no more cameras to send him data 
            if (eventsToProcess.isEmpty() && FusionSlam.getInstance().getCamerasCounter() == 0){
                sendBroadcast(new TerminatedBroadcast(getName()));
                terminate();
            }
            // As long as we have detected objects that can be send at this moment
            else{
                List<TrackedObject> trackedObjects = new LinkedList<>();
                while (!eventsToProcess.isEmpty() && eventsToProcess.peek().getTimeOfDetectedObjects() + lidarWorker.getFrequency() <= currentTick){
                    DetectObjectsEvent dob = eventsToProcess.poll();
                    StampedDetectedObjects toProcess = dob.getStampedDetectedObjects();
                    List<DetectedObject> detectedObjects = toProcess.getDetectedObjects();
                    int time = toProcess.getTime();
                    for (DetectedObject doe : detectedObjects){
                        trackedObjects.add(lidarWorker.detectToTrack(doe,time,getName()));
                    }
                    complete(dob, true);
                }
                if (!trackedObjects.isEmpty()){
                    TrackedObjectsEvent tracked = new TrackedObjectsEvent(trackedObjects,getName()); // Sends event to fusion slum
                    StatisticalFolder.getInstance().incrementTrackedObjects(trackedObjects.size());
                    lidarWorker.setLastTrackedObjectList(trackedObjects);
                    sendEvent(tracked);
                }
            }
        }
    }

    // private void processDetectedObjectEvent(DetectObjectsEvent ev){
    //     StampedDetectedObjects toProcess = ev.getStampedDetectedObjects();
    //     List<DetectedObject> detectedObjects = toProcess.getDetectedObjects();
    //     int time = toProcess.getTime();
    //     for (DetectedObject doe : detectedObjects){
    //         trackedObjects.add(lidarWorker.detectToTrack(doe,time));
    //     }
    //     complete(ev, true);
    // }

    // Gets detected DetectObjectsEvent ready rigth now to be sent, and return correspondent List of TrackedObjects  
    private List<TrackedObject> processDetectedObjects(DetectObjectsEvent ev){
        List<TrackedObject> trackedObjectsToSend = new LinkedList<>();
        StampedDetectedObjects toProcess = ev.getStampedDetectedObjects(); // Extract the StampedDetectedObjects themself
        List<DetectedObject> detectedObjects = toProcess.getDetectedObjects();  // Extract the DetectedObjects themself
        int time = toProcess.getTime();
        for (DetectedObject doe : detectedObjects){
            trackedObjectsToSend.add(lidarWorker.detectToTrack(doe,time,getName()));
        }
        lidarWorker.setLastTrackedObjectList(trackedObjectsToSend);
        complete(ev, true);
        return trackedObjectsToSend;
    }
}
