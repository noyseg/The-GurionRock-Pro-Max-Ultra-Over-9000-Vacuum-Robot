package bgu.spl.mics.application.objects;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import bgu.spl.mics.MicroService;

/**
 * Manages the fusion of sensor data for simultaneous localization and mapping
 * (SLAM).
 * Combines data from multiple sensors (e.g., LiDAR, camera) to build and update
 * a global map.
 * Implements the Singleton pattern to ensure a single instance of FusionSlam
 * exists.
 */
public class FusionSlam {
    // Singleton instance holder
    private static class FusionSlamHolder {
        private static FusionSlam instance = new FusionSlam();
    }
    private final HashMap<String, LandMark> landMarks;
    private final List<Pose> poses;
    private final AtomicInteger cameraCounter;
    private final AtomicInteger microservicesCounter;

    private FusionSlam() {
        this.landMarks = new HashMap<String, LandMark>();
        this.poses = new LinkedList<Pose>();
        this.cameraCounter = new AtomicInteger(0);
        this.microservicesCounter = new AtomicInteger(0);
    }

    public static FusionSlam getInstance() {
        return FusionSlamHolder.instance;
    }

    // Methods for Microservices Count:
    /**
     * Increments the count of active microservices.
     */
    public void incrementMicroserviceCount() {
        microservicesCounter.incrementAndGet();
    }

    /**
     * Decrements the count of active microservices.
     */
    public void decrementMicroserviceCount() {
        microservicesCounter.decrementAndGet();
    }

        // Methods for Cameras Count:
    /**
     * Increments the count of active cameras.
     */
    public void incrementCameraCount() {
        cameraCounter.incrementAndGet();
    }

    /**
     * Decrements the count of active cameras.
     */
    public void decrementCameraCount() {
        cameraCounter.decrementAndGet();
    }

    

    public HashMap<String, LandMark> getLandMarks(){
        return this.landMarks;
    }

    public List<Pose> getPoses(){
        return this.poses;
    }

    // Fix if they are not in the same size
    public void addLandMark(LandMark lm) {
        landMarks.put(lm.getId(), lm);
    }

    public void addPose(Pose p){
        poses.add(p);
    }




    // Fix if they are not in the same size
    public void updateLandMark(LandMark landM, List<CloudPoint> improvePoints) {
        List<CloudPoint> oldCloudPoints  = this.landMarks.get(landM.getId()).getCoordinates();
        List<CloudPoint> newCloudPoints = new LinkedList<>();
        int lenImprovePoints = improvePoints.size();
        int lenOldCloudPoints = oldCloudPoints.size();
        // Calculate avg of oldCloudPoints and improvePoints and put in newCloudPoints
        for (int i = 0; i < lenImprovePoints && i < lenOldCloudPoints; i++){
            CloudPoint oldP = oldCloudPoints.get(i);
            CloudPoint impP = improvePoints.get(i);
            CloudPoint newCp = new CloudPoint((impP.getX()+oldP.getX())/2.0, (impP.getY()+oldP.getY())/2.0);
            newCloudPoints.add(newCp);
        }
        // improvePoints was longer than oldCloudPoints
        if (newCloudPoints.size() < lenImprovePoints){
            for (int i=lenOldCloudPoints; i < lenImprovePoints; i++){
                CloudPoint impP = improvePoints.get(i);
                CloudPoint newCp = new CloudPoint(impP.getX(),impP.getY());
                newCloudPoints.add(newCp);
            }
        }
        // oldCloudPoints was longer than improvePoints
        if (newCloudPoints.size() < lenOldCloudPoints){
            for (int i=lenImprovePoints; i < lenOldCloudPoints; i++){
                CloudPoint oldP = oldCloudPoints.get(i);
                CloudPoint newCp = new CloudPoint(oldP.getX(),oldP.getY());
                newCloudPoints.add(newCp);
            }
        }
        this.landMarks.get(landM.getId()).setCoordinates(newCloudPoints);
    }
    

    public List<CloudPoint> poseTranformation(Pose robotPosition,List<CloudPoint> cloudPoints){
        List<CloudPoint> newCloudPoints = new LinkedList<>();
        double xRobot = robotPosition.getX();
        double yRobot = robotPosition.getY();
        double radians = robotPosition.getYaw()*Math.PI/180;
        double cos = Math.cos(radians);
        double sin = Math.sin(radians);
        for (CloudPoint cp: cloudPoints){
            double xCloudPoint = (cos*cp.getX())-(sin*cp.getY())+xRobot;
            double yCloudPoint = (sin*cp.getX())+(cos*cp.getY())+yRobot;
            CloudPoint newCloudPoint = new CloudPoint(xCloudPoint,yCloudPoint);
            newCloudPoints.add(newCloudPoint);
        }
        return newCloudPoints;
    }

    // Get current Microservices Counter
    public int getMicroservicesCounter() {
        return microservicesCounter.get();
    }

        // Get current Microservices Counter
        public int getCamerasCounter() {
            return microservicesCounter.get();
        }

    // normal term:
    // statisticalFolder
    // landmark

    // error :
    // error description
    // sender of crashed
    // last frames
    // poses
    // statisticalFolder
}
