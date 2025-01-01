package bgu.spl.mics.application;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import bgu.spl.mics.application.objects.Camera;
import bgu.spl.mics.application.objects.CameraData;
import bgu.spl.mics.application.objects.CameraInformation;
import bgu.spl.mics.application.objects.ConfigFile;
import bgu.spl.mics.application.objects.FusionSlam;
import bgu.spl.mics.application.objects.GPSIMU;
import bgu.spl.mics.application.objects.LiDarWorkerTracker;
import bgu.spl.mics.application.objects.LidarData;
import bgu.spl.mics.application.objects.Pose;
import bgu.spl.mics.application.objects.StampedCloudPoints;
import bgu.spl.mics.application.services.CameraService;
import bgu.spl.mics.application.services.FusionSlamService;
import bgu.spl.mics.application.services.LiDarService;
import bgu.spl.mics.application.services.PoseService;
import bgu.spl.mics.application.services.TimeService;

/**
 * The main entry point for the GurionRock Pro Max Ultra Over 9000 simulation.
 * <p>
 * This class initializes the system and starts the simulation by setting up
 * services, objects, and configurations.
 * </p>
 */
public class GurionRockRunner {

    /**
     * The main method of the simulation.
     * This method sets up the necessary components, parses configuration files,
     * initializes services, and starts the simulation.
     *
     * @param args Command-line arguments. The first argument is expected to be the path to the configuration file.
     */
    public static void main(String[] args) {

        if (args.length == 0) {
            System.err.println("Error: Configuration file path is required as the first argument.");
            return;
        }

        try (FileReader mainReader = new FileReader(args[0])) {
            Gson gson = new Gson();
            ConfigFile config = gson.fromJson(mainReader, ConfigFile.class);
            System.out.println("Initializing simulation components...");
            TimeService timeService = new TimeService(config.getTickTime(), config.getDuration());
            FusionSlamService FusionSlamService = new FusionSlamService(FusionSlam.getInstance());
             // Initialize cameras
             List<CameraService> camerasServices = new LinkedList<>(); 
            if (config.getCameras() != null){
                String cameraPath = config.getCameras().getCameraData();
                List<CameraInformation> allCameras = config.getCameras().getAllCameras();
                try (FileReader reader = new FileReader(cameraPath)) {
                    Type listType = new TypeToken<List<CameraData>>() {}.getType();
                    List<CameraData> cameraData = gson.fromJson(reader, listType);
                    System.out.println("Initializing camera's components...");
                    for (CameraData camera: cameraData){
                        for (CameraInformation cameraInfo: allCameras){
                            if (camera.getCameraName().equals(cameraInfo.getCameraKey())){
                                Camera newCamera = new Camera(cameraInfo.getId(),cameraInfo.getCameraKey(), cameraInfo.getFrequency(),camera.getStampedDetected());
                                camerasServices.add(new CameraService(newCamera));
                            }
                        }
                    }
                }
                catch (IOException e) {
                    System.err.println("Failed to load Camera's Data: " + e.getMessage());
                }
            }
            List<LiDarService> LiDarServices = new LinkedList<>(); 
            if (config.getLidars() != null) {
                System.out.println("Initializing lidar's components...");
                String lidarPath = config.getLidars().getLidarDataPath();
                List<LidarData> allLidar = config.getLidars().getLidars();
                for (LidarData lidar: allLidar){
                    LiDarWorkerTracker newLidar = new LiDarWorkerTracker(lidar.getId(),lidar.getFrequency(), lidarPath);
                    LiDarServices.add(new LiDarService(newLidar));
                }
            }
            List<PoseService> poseService = new LinkedList<>();
            if (config.getPoseFilePath() != null){
                try (FileReader reader = new FileReader(config.getPoseFilePath())) {
                    Type listType = new TypeToken<List<Pose>>() {}.getType();
                    List<Pose> poses = gson.fromJson(reader, listType);
                    System.out.println("Initializing GPSIMU's components...");
                    GPSIMU gps = new GPSIMU(0, poses);
                    poseService.add(new PoseService(gps));
                }
                catch (IOException e) {
                    System.err.println("Failed to load Poses's Data: " + e.getMessage());
                }
            }
            FusionSlam.getInstance().setCameraCount(camerasServices.size());
            FusionSlam.getInstance().setCameraCount(camerasServices.size()+poseService.size()+LiDarServices.size());




            System.out.println("Simulation initialized. Starting simulation loop...");
            // Start the simulation loop or time service (not implemented here)
        } catch (IOException e) {
            System.err.println("Failed to load configuration file: " + e.getMessage());
        }
    }
}
