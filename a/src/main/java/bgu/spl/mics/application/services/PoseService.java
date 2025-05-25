package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.PoseEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.TerminatedBroadcast;

import bgu.spl.mics.application.objects.GPSIMU;
import bgu.spl.mics.application.objects.STATUS;

/**
 * PoseService is responsible for maintaining the robot's current pose (position and orientation)
 * and broadcasting PoseEvents at every tick.
 */
public class PoseService extends MicroService {
    private final GPSIMU gpsimu;

    /**
     * Constructor for PoseService.
     *
     * @param gpsimu The GPSIMU object that provides the robot's pose data.
     */
    public PoseService(GPSIMU gpsimu) {
        super("GPSIMU");
        this.gpsimu = gpsimu;
    }

    /**
     * Initializes the PoseService.
     * Subscribes to TickBroadcast and sends PoseEvents at every tick based on the current pose.
     */
    @Override
    protected void initialize() {

        // Handle TickBroadcast to process each tick and generate PoseEvents.
        subscribeBroadcast(TickBroadcast.class, tick -> {
            // Process tick if the GPSIMU system is up.
            if (gpsimu.getStatus() == STATUS.UP) {
                PoseEvent poseE = gpsimu.processTick(tick);
                if (poseE != null){
                    sendEvent(poseE);
                }
            }
            if (gpsimu.getStatus() == STATUS.DOWN){
                sendBroadcast(new TerminatedBroadcast(getName()));
                terminate();
            }
        });

        // Handle CrashedBroadcast to shut down GPSIMU if a crash occurs.
        subscribeBroadcast(CrashedBroadcast.class, crash -> {
            gpsimu.setStatus(STATUS.DOWN);
            sendBroadcast(new TerminatedBroadcast(getName()));
            terminate();
        });

        // Handle TerminatedBroadcast, update its lidarCount and cameraCount if needed  
        subscribeBroadcast(TerminatedBroadcast.class, terminate -> {
            // If a lidar service terminates, decrement the lidar count.
            if (terminate.getSenderName().startsWith("L")) {
                gpsimu.decrementLidarCount();
            }
            // If a camera service terminates, decrement the camera count.
            if (terminate.getSenderName().startsWith("C")) {
                gpsimu.decrementCameraCount();
            }
            // If the TimeService terminates, set the GPSIMU status to DOWN and terminate the service.
            if (terminate.getSenderName().equals("TimeService")) {
                gpsimu.setStatus(STATUS.DOWN);
                sendBroadcast(new TerminatedBroadcast(getName()));
                terminate();
            }
        });
    }
}
