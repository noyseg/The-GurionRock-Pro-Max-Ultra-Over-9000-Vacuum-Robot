package bgu.spl.mics.application.services;

import java.util.LinkedList;

import bgu.spl.mics.MicroService;

import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.DetectObjectsEvent;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;

import bgu.spl.mics.application.objects.Camera;
import bgu.spl.mics.application.objects.CameraProcessed;
import bgu.spl.mics.application.objects.DetectedObject;
import bgu.spl.mics.application.objects.ErrorCoordinator;
import bgu.spl.mics.application.objects.STATUS;
import bgu.spl.mics.application.objects.StampedDetectedObjects;
import bgu.spl.mics.application.objects.StatisticalFolder;

/**
 * CameraService is responsible for processing data from the camera and
 * sending DetectObjectsEvents to LiDAR workers.
 * 
 * This service interacts with the Camera object to detect objects and updates
 * the system's StatisticalFolder upon sending its observations.
 */
public class CameraService extends MicroService {
    private boolean hasDetectionsRemaining; // Indicates if the camera has detections remaining
    private final Camera camera; // The camera object associated with this service
    private LinkedList<CameraProcessed> waitingQueue ; // Queue of camera's waiting data to be sent
    private StampedDetectedObjects lastDetectedObjects; // The last detected objects by the camera

    /**
     * Constructor for CameraService.
     *
     * @param camera The Camera object that this service will use to detect objects.
     */
    public CameraService(Camera camera) {
        super(camera.getName());
        this.hasDetectionsRemaining = true;
        this.camera = camera;
        this.waitingQueue = new LinkedList<>();
        lastDetectedObjects = new StampedDetectedObjects();
    }

    /**
     * Initializes the CameraService.
     * Registers the service to handle TickBroadcasts and sets up callbacks for
     * sending
     * DetectObjectsEvents.
     */
    @Override
    protected void initialize() {
        System.out.println(getName() + " started");

        // Subscribe to TickBroadcast
        subscribeBroadcast(TickBroadcast.class, tick -> {
            if (camera.getStatus() == STATUS.UP) {
                // Process only if the camera is active.
                processTick(tick);
            }
        });

         // Subscribe to TerminatedBroadcast to shut down when TimeService ends.
        subscribeBroadcast(TerminatedBroadcast.class, terminate -> {
            if (terminate.getSenderName().equals("TimeService")) {
                camera.setStatus(STATUS.DOWN);
                sendBroadcast(new TerminatedBroadcast(getName()));
                terminate();
            }
        });

         // Subscribe to CrashedBroadcast in order to terminate in case of a sensor's crash
        subscribeBroadcast(CrashedBroadcast.class, crash -> {
            camera.setStatus(STATUS.DOWN);
            sendBroadcast(new TerminatedBroadcast(getName()));
            ErrorCoordinator.getInstance().setLastFramesCameras(getName(), lastDetectedObjects);
            terminate();
        });
    }

    /**
     * Processes a TickBroadcast to detect objects at the appropriate frequency.
     *
     * @param tick The TickBroadcast containing the current time.
     */
    private void processTick(TickBroadcast tick) { 
         // Check if there are detections remaining
        if (camera.getDetectedObjectsList().size() == 0){
            hasDetectionsRemaining = false;
        }
        if (hasDetectionsRemaining){
            // Potential detected objects at tick time 
            StampedDetectedObjects nextDetectedObjects = camera.getDetectedObjectsList().get(0);
            int currentTickTime = tick.getCurrentTime();
            
            if (currentTickTime == nextDetectedObjects.getTime()) {
                checkForError(nextDetectedObjects, currentTickTime);
                // Case that no error was detected 
                if (camera.getStatus() == STATUS.UP){
                    putOnWaitingQueue(nextDetectedObjects, currentTickTime);
                }
            }
        }
        // Objects are ready to be sent to lidar  
        detectionToSend(tick.getCurrentTime());
        // Checks if camera finishs its job and terminate in case it is
        checkIfFinishAndTerminate();
    }

     /**
     * Checks if there are any errors in the detected objects.
     *
     * @param detectedObjects The detected objects to check.
     * @param tickTime        The current tick time.
     * / Sends crash brodcast in case of error detection 
     */
    private void checkForError(StampedDetectedObjects nextDetectedObjects, int tickTime){
        for (DetectedObject dob : nextDetectedObjects.getDetectedObjects()) {
            // Error was detected 
            if (dob.getId().equals("ERROR")) {
                camera.setStatus(STATUS.ERROR);
                sendBroadcast(new CrashedBroadcast(getName()));
                //LastFrameCamera lf = new LastFrameCamera(getName(),lastDetectedObjTime ,lastDetectedObj);
                ErrorCoordinator.getInstance().setLastFramesCameras(getName(), lastDetectedObjects);
                ErrorCoordinator.getInstance().setCrashed(getName(), tickTime, dob.getDescription());
                terminate();
            }
        }
    }

    /**
     * Adds detected objects to the waiting queue for processing and updates system statistics.
     *
     * @param nextDetectedObjects The detected objects to be added to the waiting list.
     * @param tickTime The current tick time when the objects were detected.
     */
    private void putOnWaitingQueue(StampedDetectedObjects nextDetectedObjects, int tickTime){

        // Calculate the processing time based on the camera's frequency
        int processingTime = tickTime + camera.getFrequency();

        // Create a CameraProcessed object with the processing time and detected objects
        
        CameraProcessed processedObject = new CameraProcessed(processingTime, nextDetectedObjects);
        // Add the processed object to the waiting list
        waitingQueue.add(processedObject);
        lastDetectedObjects = nextDetectedObjects;
        // Remove the detected objects from the camera's list and update statistics
        int detectedObjectCount = camera.getDetectedObjectsList().remove(0).getDetectedObjects().size();
        StatisticalFolder.getInstance().incrementDetectedObjects(detectedObjectCount);
    }

    /**
     * Sends detected objects to the LiDAR service if their processing time matches the current tick.
     *
     * @param tickTime The current tick time 
     */
    private void detectionToSend(int tickTime){

        // Process detections that are ready to be sent at the current tick
        if (camera.getStatus() == STATUS.UP && !waitingQueue.isEmpty() && waitingQueue.getFirst().getProcessionTime() == tickTime){
            
            // Remove the first waiting detection from the queue
            CameraProcessed toLidar = waitingQueue.removeFirst();
            
            // Extract the stamped detected objects
            StampedDetectedObjects stampedToLiDar = toLidar.getDetectedObject();

            // Create a DetectObjectsEvent
            DetectObjectsEvent doe = new DetectObjectsEvent(stampedToLiDar, stampedToLiDar.getTime(), getName());
            
            sendEvent(doe);
        }
    }

    /**
     * Checks if the camera has finished all its detections and terminates the service if so.
     * Conditions for termination:
     * - The camera's status is `UP` (still active).
     * - There are no remaining detections.
     * - All waiting objects have been sent.
     */
    private void checkIfFinishAndTerminate(){
        if (camera.getStatus() == STATUS.UP && !hasDetectionsRemaining && waitingQueue.isEmpty()) {
            camera.setStatus(STATUS.DOWN);
            ErrorCoordinator.getInstance().setLastFramesCameras(getName(), lastDetectedObjects);
            sendBroadcast(new TerminatedBroadcast(getName()));
            terminate();
        }
    }
}
