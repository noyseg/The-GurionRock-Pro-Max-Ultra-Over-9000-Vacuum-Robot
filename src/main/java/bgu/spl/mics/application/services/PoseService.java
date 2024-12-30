package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.PoseEvent;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.GPSIMU;
import bgu.spl.mics.application.objects.Pose;
import bgu.spl.mics.application.objects.STATUS;

/**
 * PoseService is responsible for maintaining the robot's current pose (position and orientation)
 * and broadcasting PoseEvents at every tick.
 */
public class PoseService extends MicroService {
    private final GPSIMU gpsimu;
    private Pose currentPose;

    /**
     * Constructor for PoseService.
     *
     * @param gpsimu The GPSIMU object that provides the robot's pose data.
     */
    public PoseService(GPSIMU gpsimu) {
        super("GPSIMU");
        this.gpsimu = gpsimu;
        this.currentPose = null; // do it first at pose 0 or 1 
    }

    /**
     * Initializes the PoseService.
     * Subscribes to TickBroadcast and sends PoseEvents at every tick based on the current pose.
     */
    @Override
    protected void initialize() {
        subscribeBroadcast(TickBroadcast.class, tick -> {
            processTick(tick);
        });

        // Handle CrashedBroadcast (if needed)
        subscribeBroadcast(CrashedBroadcast.class, crash -> {
            sendBroadcast(new TerminatedBroadcast(getName(),getName()));
            // Print the most recent poses? for the current camera
        });

    }

    private void setPose(){
        this.currentPose = gpsimu.getPose();
    }

    private void processTick(TickBroadcast tick) {
        gpsimu.setCurrentTick(tick.getCurrentTime());
        setPose();
        PoseEvent poseEvent = new PoseEvent(currentPose,getName());
        Future<Boolean> future = (Future<Boolean>) sendEvent(poseEvent);
        try {
            if (future.get() == false) {
                System.out.println("Fusion Slum could not get Point");
                sendBroadcast(new TerminatedBroadcast(getName(),getName()));
                terminate();
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendBroadcast(new CrashedBroadcast(getName(),getName())); // why this the handle? itself did not crash 
        }
    }
}
