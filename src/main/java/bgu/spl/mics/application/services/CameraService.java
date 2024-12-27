package bgu.spl.mics.application.services;

import java.util.LinkedList;
import java.util.List;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.DetectObjectsEvent;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.Camera;
import bgu.spl.mics.application.objects.CameraProcessed;
import bgu.spl.mics.application.objects.DetectedObject;
import bgu.spl.mics.application.objects.STATUS;
import bgu.spl.mics.application.objects.StampedDetectedObjects;
import bgu.spl.mics.example.messages.ExampleBroadcast;

/**
 * CameraService is responsible for processing data from the camera and
 * sending DetectObjectsEvents to LiDAR workers.
 * 
 * This service interacts with the Camera object to detect objects and updates
 * the system's StatisticalFolder upon sending its observations.
 */
public class CameraService extends MicroService {
    private final Camera camera;
    private LinkedList<CameraProcessed> cpList; // List of camera data objects (time stamp+freq and detectedObjects)
    private List<DetectedObject> lastDetectedObj; 
    /**
     * Constructor for CameraService.
     *
     * @param camera The Camera object that this service will use to detect objects.
     */
    public CameraService(Camera camera) {
        super("Camera" + camera.getID());
        this.camera = camera;
        this.cpList = new LinkedList<>();
        lastDetectedObj = new LinkedList<DetectedObject>();
    }

    /**
     * Initializes the CameraService.
     * Registers the service to handle TickBroadcasts and sets up callbacks for
     * sending
     * DetectObjectsEvents.
     */
    @Override
    protected void initialize() {
        // Subscribe to TickBroadcast, TerminatedBroadcast, CrashedBroadcast.
        subscribeBroadcast(TickBroadcast.class, tick -> {
            if (camera.getStatus() == STATUS.UP) {
                processTick(tick);
            }
        });

        // Subscribe to TerminateBroadcast to gracefully shut down
        subscribeBroadcast(TerminatedBroadcast.class, terminate -> {
            camera.setStatus(STATUS.DOWN);
            if (terminate.getSenderId().equals("TimeService")) {
                sendBroadcast(new TerminatedBroadcast(getName()));
                terminate();
            }
        });

        // Handle CrashedBroadcast (if needed)
        subscribeBroadcast(CrashedBroadcast.class, crash -> {
            //Print the most recent detectedObjects for the current camera

        });
    }

    /**
     * Processes a TickBroadcast to detect objects at the appropriate frequency.
     *
     * @param tick The TickBroadcast containing the current time.
     */
    private void processTick(TickBroadcast tick) {
        for (StampedDetectedObjects stampedDetectedObjects : camera.getDetectedObjectsList()) {
            for (DetectedObject dob : stampedDetectedObjects.getDetectedObjects()) {
                if (dob.getID() == "ERROR") {
                    System.out.println("ERROR -"+dob.getDescription());
                    System.out.println("Camera Sensor with the name"+getName()+" caused the error");
                    System.out.println("the last detected object caught by the cemra are: ");//להשלים
                    sendBroadcast(new CrashedBroadcast(getName()));
                }
            }
        }
        // לחשוב לשנות לפתרון נאיבי שעובר בלולאה על הרשימה במצלמה
        List<DetectedObject> nextDetectedObjects = camera.getDetectedObjectsList().get(0).getDetectedObjects();
        int timeOfDetectedObjects = camera.getDetectedObjectsList().get(0).getTime();
        if (nextDetectedObjects != null && tick.getCurrentTime() == timeOfDetectedObjects + camera.getFrequency()) {
            DetectObjectsEvent doe = new DetectObjectsEvent(timeOfDetectedObjects,
                    nextDetectedObjects);
            Future<Boolean> future = (Future<Boolean>) sendEvent(doe);
            lastDetectedObj = nextDetectedObjects;
            try {
                if (future.get() == false) {
                    System.out.println("No LiDar manage to tracked the objects");
                }
            } catch (InterruptedException ie) {
                sendBroadcast(new CrashedBroadcast(getName()));
            }
            camera.getDetectedObjectsList().remove(0);
        }

    }
}
