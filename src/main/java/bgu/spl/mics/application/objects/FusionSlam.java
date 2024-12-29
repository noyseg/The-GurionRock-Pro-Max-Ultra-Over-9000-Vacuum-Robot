package bgu.spl.mics.application.objects;

import java.util.LinkedList;
import java.util.List;

/**
 * Manages the fusion of sensor data for simultaneous localization and mapping (SLAM).
 * Combines data from multiple sensors (e.g., LiDAR, camera) to build and update a global map.
 * Implements the Singleton pattern to ensure a single instance of FusionSlam exists.
 */
public class FusionSlam {
    // Singleton instance holder
    private static class FusionSlamHolder {
        private static instance = new FusionSlam();
    }
        private final List<LandMark> landMarks;
        private final List<Pose> poses;
        private StatisticalFolder statisticalFolder;

        public FusionSlam(){
            this.landMarks = new LinkedList<>();
            this.poses = new LinkedList<>();
            this. 
        }


    }
}
