package bgu.spl.mics.application.services;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.PoseEvent;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.TrackedObjectsEvent;
import bgu.spl.mics.application.objects.FusionSlam;
import bgu.spl.mics.application.objects.LandMark;
import bgu.spl.mics.application.objects.Pose;
import bgu.spl.mics.application.objects.StatisticalFolder;
import bgu.spl.mics.application.objects.TrackedObject;

/**
 * FusionSlamService integrates data from multiple sensors to build and update
 * the robot's global map.
 * 
 * This service receives TrackedObjectsEvents from LiDAR workers and PoseEvents from the PoseService,
 * transforming and updating the map with new landmarks.
 */
public class FusionSlamService extends MicroService {
    private final FusionSlam fusionSlam;

    /**
     * Constructor for FusionSlamService.
     *
     * @param fusionSlam The FusionSLAM object responsible for managing the global map.
     */
    public FusionSlamService(FusionSlam fusionSlam) {
        super("FusionSlam");
        this.fusionSlam = fusionSlam;
    }

    /**
     * Initializes the FusionSlamService.
     * Registers the service to handle TrackedObjectsEvents, PoseEvents, and TickBroadcasts,
     * and sets up callbacks for updating the global map.
     */
    @Override
    protected void initialize() {
        
        System.out.println(getName() + " started");
        subscribeBroadcast(TickBroadcast.class, tick -> {


        });
        subscribeEvent(TrackedObjectsEvent.class, trackedObj -> { 

        });
        subscribeEvent(PoseEvent.class, pose -> { 

        });
        subscribeBroadcast(TerminatedBroadcast.class, terminate -> { 

        });
        subscribeBroadcast(CrashedBroadcast.class, crash -> { 

        });

        // Subscribes to TickBroadcast, TrackedObjectsEvent, PoseEvent, TerminatedBroadcast,CrashedBroadcast
    }

    private void handleLandMarks(trackedObj){
        TrackedObject trackedObjects = trackedObj.getTrackedObjects();
        for (TrackedObject to: trackedObjects){
            
        }
    }
}
