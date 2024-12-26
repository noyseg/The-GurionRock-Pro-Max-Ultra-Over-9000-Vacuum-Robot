package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
<<<<<<< HEAD
=======
import bgu.spl.mics.application.objects.Camera;
>>>>>>> 9adfc1fa37254a4a9c8910abb32cdc95fb1e48a6

/**
 * CameraService is responsible for processing data from the camera and
 * sending DetectObjectsEvents to LiDAR workers.
 * 
 * This service interacts with the Camera object to detect objects and updates
 * the system's StatisticalFolder upon sending its observations.
 */
public class CameraService extends MicroService {

    /**
     * Constructor for CameraService.
     *
     * @param camera The Camera object that this service will use to detect objects.
     */
    public CameraService(Camera camera) {
        super("Change_This_Name");
        // TODO Implement this
    }

    /**
     * Initializes the CameraService.
     * Registers the service to handle TickBroadcasts and sets up callbacks for sending
     * DetectObjectsEvents.
     */
    @Override
    protected void initialize() {
<<<<<<< HEAD
        // TODO Implement this
=======
        // Subscribe to TickBroadcast, TerminatedBroadcast, CrashedBroadcast.
>>>>>>> 9adfc1fa37254a4a9c8910abb32cdc95fb1e48a6
    }
}
