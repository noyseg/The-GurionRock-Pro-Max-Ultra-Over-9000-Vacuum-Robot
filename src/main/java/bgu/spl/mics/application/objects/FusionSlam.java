package bgu.spl.mics.application.objects;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import bgu.spl.mics.MicroService;

/**
 * Manages the fusion of sensor data for simultaneous localization and mapping (SLAM).
 * Combines data from multiple sensors (e.g., LiDAR, camera) to build and update a global map.
 * Implements the Singleton pattern to ensure a single instance of FusionSlam exists.
 */
public class FusionSlam {
    // Singleton instance holder
    private static class FusionSlamHolder {
        private static FusionSlam instance = new FusionSlam();
    }
        private final List<LandMark> landMarks;
        private final List<Pose> poses;
        private final StatisticalFolder statisticalFolder;
        private AtomicInteger microservicesCounter;

        private FusionSlam(){
            this.landMarks = new LinkedList<LandMark>();
            this.poses = new LinkedList<Pose>();
            this.statisticalFolder = StatisticalFolder.getInstance();
            this.microservicesCounter =new AtomicInteger(0);
        }

        public static FusionSlam getInstance(){
            return FusionSlamHolder.instance;
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
