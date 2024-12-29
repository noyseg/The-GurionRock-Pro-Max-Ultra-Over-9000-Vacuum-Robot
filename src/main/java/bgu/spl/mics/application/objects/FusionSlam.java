package bgu.spl.mics.application.objects;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

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
    private final StatisticalFolder statisticalFolder;
    private AtomicInteger microservicesCounter;

    private FusionSlam() {
        this.landMarks = new HashMap<String, LandMark>();
        this.poses = new LinkedList<Pose>();
        this.statisticalFolder = StatisticalFolder.getInstance();
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

    public void addeLandMark() {

    }

    public void updateLandMark() {

    }

    public CloudPoint poseTranformation(Pose robotPosition,float xDetected,float yDetected){
        double xRobot = robotPosition.getX();
        double yRobot = robotPosition.getY();
        double radians = robotPosition.getYaw()*Math.PI/180;
        double cos = Math.cos(radians);
        double sin = Math.sin(radians);
        double xCloudPoint = (cos*xDetected)-(sin*yDetected)+xRobot;
        double yCloudPoint = (sin*xDetected)+(cos*yDetected)+yRobot;
        CloudPoint newCloudPoint = new CloudPoint(xCloudPoint,yCloudPoint);
        return newCloudPoint;
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
