package bgu.spl.mics.application.services;

import java.util.List;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.DetectObjectsEvent;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.Camera;
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

    /**
     * Constructor for CameraService.
     *
     * @param camera The Camera object that this service will use to detect objects.
     */
    public CameraService(Camera camera) {
        super(camera.getName());
        this.camera = camera;
        // TODO Implement this

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
        subscribeBroadcast(TickBroadcast.class,  tick -> {
            if (camera.getStatus() == STATUS.UP) {
                processTick(tick);
            }
        });

        // Subscribe to TerminateBroadcast to gracefully shut down
        subscribeBroadcast(TerminatedBroadcast.class, terminate -> {
            terminate(); // Shutdown the MicroService
        });

        // Handle CrashedBroadcast (if needed)
        subscribeBroadcast(CrashedBroadcast.class, crash -> {
            camera.setStatus(STATUS.ERROR);
        });
    }

     /**
     * Processes a TickBroadcast to detect objects at the appropriate frequency.
     *
     * @param tick The TickBroadcast containing the current time.
     */
    private void processTick(TickBroadcast tick) {
        // Check if the camera should detect objects at this tick
        if (tick.getCurrentTime() % camera.getFrequency() == 0) {
            // Simulate object detection
            List<StampedDetectedObjects> detectedObjects = camera.getDetectedObjectsList();

            // Create and send DetectObjectsEvent for each detected object
            for (StampedDetectedObjects object : detectedObjects) {
                // sendEvent(new DetectObjectsEvent(object));
            }
        }
    }
}
