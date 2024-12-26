package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
<<<<<<< HEAD
=======
import bgu.spl.mics.application.objects.GPSIMU;
>>>>>>> 9adfc1fa37254a4a9c8910abb32cdc95fb1e48a6

/**
 * PoseService is responsible for maintaining the robot's current pose (position and orientation)
 * and broadcasting PoseEvents at every tick.
 */
public class PoseService extends MicroService {

    /**
     * Constructor for PoseService.
     *
     * @param gpsimu The GPSIMU object that provides the robot's pose data.
     */
    public PoseService(GPSIMU gpsimu) {
        super("Change_This_Name");
        // TODO Implement this
    }

    /**
     * Initializes the PoseService.
     * Subscribes to TickBroadcast and sends PoseEvents at every tick based on the current pose.
     */
    @Override
    protected void initialize() {
<<<<<<< HEAD
        // TODO Implement this
=======
        // Subscribes to TickBroadcast.
>>>>>>> 9adfc1fa37254a4a9c8910abb32cdc95fb1e48a6
    }
}
