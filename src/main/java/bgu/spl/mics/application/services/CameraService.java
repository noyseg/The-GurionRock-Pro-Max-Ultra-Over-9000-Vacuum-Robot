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
    // private List<DetectedObject> lastDetectedObj;

    /**
     * Constructor for CameraService.
     *
     * @param camera The Camera object that this service will use to detect objects.
     */
    public CameraService(Camera camera) {
        super("Camera" + camera.getID());
        this.camera = camera;
        this.cpList = new LinkedList<>();
        // lastDetectedObj = new LinkedList<DetectedObject>();
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
            if (terminate.getSenderId().equals("TimeService")) {
                camera.setStatus(STATUS.DOWN);
                sendBroadcast(new TerminatedBroadcast(getName()));
                terminate();
            }
        });

        // Handle CrashedBroadcast (if needed)
        subscribeBroadcast(CrashedBroadcast.class, crash -> {
            camera.setStatus(STATUS.DOWN);
            sendBroadcast(new TerminatedBroadcast(getName()));
            // Print the most recent detectedObjects for the current camera

        });
    }

    /**
     * Processes a TickBroadcast to detect objects at the appropriate frequency.
     *
     * @param tick The TickBroadcast containing the current time.
     */
    private void processTick(TickBroadcast tick) {
        StampedDetectedObjects nextDetectedObjects = camera.getDetectedObjectsList().get(0);
        int timeOfDetectedObjects = nextDetectedObjects.getTime();
        if (tick.getCurrentTime() == timeOfDetectedObjects) {
            for (DetectedObject dob : nextDetectedObjects.getDetectedObjects()) {
                if (dob.getID() == "ERROR") {
                    System.out.println("ERROR -" + dob.getDescription());
                    System.out.println("Camera Sensor with the name" + getName() + " caused the error");
                    System.out.println("the last detected object caught by the cemra are: ");// להשלים
                    camera.setStatus(STATUS.ERROR);
                    sendBroadcast(new CrashedBroadcast(getName()));
                    terminate();
                }
            }
            CameraProcessed dobjWithFreq = new CameraProcessed(tick.getCurrentTime() + camera.getFrequency(),
                    nextDetectedObjects);
            cpList.add(dobjWithFreq);
            camera.addNumDetectedObjects(camera.getDetectedObjectsList().remove(0).getDetectedObjects().size());
        }
        // לחשוב לשנות לפתרון נאיבי שעובר בלולאה על הרשימה במצלמה

        // lastDetectedObj =
        // camera.getDetectedObjectsList().get(0).getDetectedObjects();
        if (cpList.getFirst() != null
                && cpList.getFirst().getProcessionTime() == tick.getCurrentTime())

        {
            CameraProcessed toLidar = cpList.removeFirst();
            StampedDetectedObjects stampedToLiDar = toLidar.getDetectedObject();
            DetectObjectsEvent doe = new DetectObjectsEvent(stampedToLiDar, tick.getCurrentTime(),getName());
            Future<Boolean> future = (Future<Boolean>) sendEvent(doe);
            try {
                if (future.get() == false) {
                    System.out.println("No LiDar manage to tracked the objects");
                    sendBroadcast(new TerminatedBroadcast(getName()));
                    terminate();
                }
            } catch (Exception e) {
                e.printStackTrace();
                sendBroadcast(new CrashedBroadcast(getName()));
            }
        }

        // if camera is empty --- לבדוק אם קורה בזמן הנוכחי או בזמן הבא
        if (camera.getDetectedObjectsList().isEmpty()) {
            camera.setStatus(STATUS.DOWN);
            sendBroadcast(new TerminatedBroadcast(getName()));
            terminate();
        }

    }
}
