package bgu.spl.mics.application.services;
import java.util.LinkedList;
import java.util.List;
import bgu.spl.mics.application.objects.StatisticalFolder;
import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.DetectObjectsEvent;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.Camera;
import bgu.spl.mics.application.objects.CameraProcessed;
import bgu.spl.mics.application.objects.DetectedObject;
import bgu.spl.mics.application.objects.ErrorCoordinator;
import bgu.spl.mics.application.objects.LastFrameCamera;
import bgu.spl.mics.application.objects.STATUS;
import bgu.spl.mics.application.objects.StampedDetectedObjects;

/**
 * CameraService is responsible for processing data from the camera and
 * sending DetectObjectsEvents to LiDAR workers.
 * 
 * This service interacts with the Camera object to detect objects and updates
 * the system's StatisticalFolder upon sending its observations.
 */
public class CameraService extends MicroService {
    private boolean dections;
    private final Camera camera;
    private LinkedList<CameraProcessed> cameraProcessedList; // List of camera data objects (time stamp+freq and detectedObjects)
    private StampedDetectedObjects lastDetectedObj;
    //private int lastDetectedObjTime;

    /**
     * Constructor for CameraService.
     *
     * @param camera The Camera object that this service will use to detect objects.
     */
    public CameraService(Camera camera) {
        super(camera.getName());
        this.dections = true;
        this.camera = camera;
        this.cameraProcessedList = new LinkedList<>();
        lastDetectedObj = new StampedDetectedObjects();
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
        // Handle TickBroadcast
        subscribeBroadcast(TickBroadcast.class, tick -> {
            if (camera.getStatus() == STATUS.UP) {
                processTick(tick);
            }
        });

        // Handle TerminatedBroadcast
        subscribeBroadcast(TerminatedBroadcast.class, terminate -> {
            if (terminate.getSenderName().equals("TimeService")) {
                camera.setStatus(STATUS.DOWN);
                sendBroadcast(new TerminatedBroadcast(getName()));
                terminate();
            }
        });

        // Handle CrashedBroadcast 
        subscribeBroadcast(CrashedBroadcast.class, crash -> {
            camera.setStatus(STATUS.DOWN);
            sendBroadcast(new TerminatedBroadcast(getName()));
            //LastFrameCamera lf = new LastFrameCamera(getName(), lastDetectedObjTime, lastDetectedObj);
            ErrorCoordinator.getInstance().setLastFramesCameras(getName(), lastDetectedObj);
            terminate();
        });
    }

    /**
     * Processes a TickBroadcast to detect objects at the appropriate frequency.
     *
     * @param tick The TickBroadcast containing the current time.
     */
    private void processTick(TickBroadcast tick) {

        // Potential detected objects at tick time 
        if (camera.getDetectedObjectsList().size() == 0){
            dections = false;
        }
        if (dections){
            StampedDetectedObjects nextDetectedObjects = camera.getDetectedObjectsList().get(0);
            int tickTime = tick.getCurrentTime();
            int timeOfDetectedObjects = nextDetectedObjects.getTime();
            // Proccesing the images that the camera detect in current time, if exist. 
            if (tickTime == timeOfDetectedObjects) {
                for (DetectedObject dob : nextDetectedObjects.getDetectedObjects()) {
                    // Error was detected 
                    if (dob.getID().equals("ERROR")) {
                        camera.setStatus(STATUS.ERROR);
                        sendBroadcast(new CrashedBroadcast(getName()));
                        //LastFrameCamera lf = new LastFrameCamera(getName(),lastDetectedObjTime ,lastDetectedObj);
                        ErrorCoordinator.getInstance().setLastFramesCameras(getName(), lastDetectedObj);
                        ErrorCoordinator.getInstance().setCrashed(getName(), tickTime, dob.getDescription());
                        terminate();
                    }
                }
                // Case that no error was detected 
                if (camera.getStatus() == STATUS.UP){
                    CameraProcessed dobjWithFreq = new CameraProcessed(tickTime + camera.getFrequency(),nextDetectedObjects);
                    cameraProcessedList.add(dobjWithFreq);
                    lastDetectedObj = nextDetectedObjects;
                    StatisticalFolder.getInstance().incrementDetectedObjects(camera.getDetectedObjectsList().remove(0).getDetectedObjects().size());
                }
            }
        }
        // Objects are ready to be sent to lidar  
        if (camera.getStatus() == STATUS.UP && !cameraProcessedList.isEmpty()  && cameraProcessedList.getFirst().getProcessionTime() == tick.getCurrentTime()){
            CameraProcessed toLidar = cameraProcessedList.removeFirst(); 
            StampedDetectedObjects stampedToLiDar = toLidar.getDetectedObject(); // The dectedObjects to be sent
            DetectObjectsEvent doe = new DetectObjectsEvent(stampedToLiDar, stampedToLiDar.getTime() ,getName());
            Future<Boolean> future = (Future<Boolean>) sendEvent(doe);
        }
        // Camera does not have any other detectedObject, it can be terminated
        if (camera.getStatus() == STATUS.UP && !dections && cameraProcessedList.isEmpty()) {
            camera.setStatus(STATUS.DOWN);
            ErrorCoordinator.getInstance().setLastFramesCameras(getName(), lastDetectedObj);
            sendBroadcast(new TerminatedBroadcast(getName()));
            terminate();
        }
    }
}
