package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;

import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.DetectObjectsEvent;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;

import bgu.spl.mics.application.objects.Camera;
import bgu.spl.mics.application.objects.ErrorCoordinator;
import bgu.spl.mics.application.objects.STATUS;

/**
 * CameraService is responsible for processing data from the camera and
 * sending DetectObjectsEvents to LiDAR workers.
 * 
 * This service interacts with the Camera object to detect objects and updates
 * the system's StatisticalFolder upon sending its observations.
 */
public class CameraService extends MicroService {
    private final Camera camera; // The camera object associated with this service

    /**
     * Constructor for CameraService.
     *
     * @param camera The Camera object that this service will use to detect objects.
     */
    public CameraService(Camera camera) {
        super(camera.getName());
        this.camera = camera;
    }

    /**
     * Initializes the CameraService.
     * Registers the service to handle TickBroadcasts and sets up callbacks for
     * sending
     * DetectObjectsEvents.
     */
    @Override
    protected void initialize() {

        // Subscribe to TickBroadcast
        subscribeBroadcast(TickBroadcast.class, tick -> {
            if (camera.getStatus() == STATUS.UP) {
                // Process only if the camera is active
                DetectObjectsEvent doe = camera.processTick(tick);
                if (doe != null){
                    sendEvent(doe); 
                }
            }
            if (camera.getStatus() == STATUS.ERROR){
                sendBroadcast(new CrashedBroadcast(getName()));
                terminate();
            }
            if (camera.getStatus() == STATUS.DOWN){
                sendBroadcast(new TerminatedBroadcast(getName()));
                terminate();
            }
        });

        // Subscribe to TerminatedBroadcast to shuts down when TimeService ends.
        subscribeBroadcast(TerminatedBroadcast.class, terminate -> {
            if (terminate.getSenderName().equals("TimeService")) {
                camera.setStatus(STATUS.DOWN);
                sendBroadcast(new TerminatedBroadcast(getName()));
                terminate();
            }
        });

        // Subscribe to CrashedBroadcast in order to terminate in case of a sensor's
        // crash
        subscribeBroadcast(CrashedBroadcast.class, crash -> {
            camera.setStatus(STATUS.DOWN);
            sendBroadcast(new TerminatedBroadcast(getName()));
            ErrorCoordinator.getInstance().setLastFramesCameras(getName(), camera.getLastDetectedObjects());
            terminate();
        });
    }
}
