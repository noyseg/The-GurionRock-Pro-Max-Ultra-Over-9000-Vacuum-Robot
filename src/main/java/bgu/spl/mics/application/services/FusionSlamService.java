package bgu.spl.mics.application.services;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.PoseEvent;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.TrackedObjectsEvent;
import bgu.spl.mics.application.objects.CloudPoint;
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
    private boolean isTimeServiceOver;
    private HashMap<> currentPose;

    /**
     * Constructor for FusionSlamService.
     *
     * @param fusionSlam The FusionSLAM object responsible for managing the global map.
     */
    public FusionSlamService(FusionSlam fusionSlam) {
        super("FusionSlam");
        this.fusionSlam = fusionSlam;
        this.isTimeServiceOver = false;
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
            updateLandMarks(trackedObj);

        });
        subscribeEvent(PoseEvent.class, pose -> {
            fusionSlam.addPose(pose.getPose());
        });

        subscribeBroadcast(TerminatedBroadcast.class, terminate -> {
            // Time Service was terminated 
            if (terminate.getSenderName() == "TimeService"){
                isTimeServiceOver = true;
                // All other microservisec was finished too 
                if (fusionSlam.getMicroservicesCounter() == 0){
                    fusionSlam.creatOutputFile();
                    terminate();
                }
            }
            else{
                // Another microservice was finished 
                fusionSlam.decrementMicroserviceCount(); 
            }
            // All other microservices finished their run
            if (fusionSlam.getMicroservicesCounter() == 0){
                // Notify Time service he need to finish 
                if (!isTimeServiceOver){
                    TerminatedBroadcast timeToFinish = new TerminatedBroadcast(getName());
                    sendBroadcast(timeToFinish); // Sends only to TimeService
                }
                else{
                    fusionSlam.creatOutputFile();
                    terminate();
                }
            }
        });

        subscribeBroadcast(CrashedBroadcast.class, crash -> { 

        });

        // Subscribes to TickBroadcast, TrackedObjectsEvent, PoseEvent, TerminatedBroadcast,CrashedBroadcast
    }

    private void processTick(TickBroadcast tick){
        StatisticalFolder.getInstance().incrementSystemRunTime(1);

    }

    private void updateLandMarks(TrackedObjectsEvent trackedObj){
        List<TrackedObject> trackedObjects = trackedObj.getTrackedObjects();
        for (TrackedObject trackO: trackedObjects){
            List<CloudPoint> globaCloudPoints = fusionSlam.poseTranformation(currentPose, trackO.getCoordinates());
            // Adding New LandMark
            if (fusionSlam.getLandMarks().get(trackO.getId()) == null){
                LandMark newLandMark = new LandMark(trackO.getId(), trackO.getDescription(), globaCloudPoints);
                fusionSlam.addLandMark(newLandMark);
                fusionSlam.getLandMarks().put(newLandMark.getId(), newLandMark);
            } 
            // Improving Coordinates 
            else{
                fusionSlam.updateLandMark(fusionSlam.getLandMarks().get(trackO.getId()),globaCloudPoints);
            }
        }
    }
}
