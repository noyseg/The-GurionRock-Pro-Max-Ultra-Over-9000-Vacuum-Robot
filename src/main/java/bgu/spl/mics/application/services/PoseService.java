package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.PoseEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.TerminatedBroadcast;

import bgu.spl.mics.application.objects.GPSIMU;
import bgu.spl.mics.application.objects.Pose;
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
            // If no cameras or lidars are left, set GPSIMU status to DOWN and terminate.
            if (gpsimu.getCameraCount() == 0 && gpsimu.getLidarCount() == 0) {
                gpsimu.setStatus(STATUS.DOWN);
                sendBroadcast(new TerminatedBroadcast(getName()));
                terminate();
            }
            // Process tick if the GPSIMU system is up.
            if (gpsimu.getStatus() == STATUS.UP) {
                processTick(tick);
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

     /**
     * Processes a tick event, updating the robot's current pose and broadcasting a PoseEvent.
     * 
     * - Sets the current tick in the GPSIMU system.
     * - If a valid pose exists for the current tick, a PoseEvent is generated and sent.
     * - If no valid pose is found, the GPSIMU status is set to DOWN and the service is terminated.
     *
     * @param tick The TickBroadcast that provides the current tick time.
     */
    private void processTick(TickBroadcast tick) {
        gpsimu.setCurrentTick(tick.getCurrentTime());
        Pose pose = gpsimu.getPose();
        
        if (pose != null) {
            // Generate and send a PoseEvent if a valid pose is available.
            PoseEvent poseEvent = new PoseEvent(gpsimu.getPose());
            gpsimu.updateLastPose();
            sendEvent(poseEvent);
        } else {
            // If no pose is found, set GPSIMU status to DOWN and terminate.
            gpsimu.setStatus(STATUS.DOWN);
            sendBroadcast(new TerminatedBroadcast(getName()));
            terminate();
        }
    }
}
