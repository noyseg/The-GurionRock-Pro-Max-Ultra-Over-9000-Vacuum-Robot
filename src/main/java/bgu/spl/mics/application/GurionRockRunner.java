package bgu.spl.mics.application;

import java.nio.file.Path;
import java.nio.file.Paths;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.application.services.*;

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
     * @param args Command-line arguments. The first argument is expected to be the
     *             path to the configuration file.
     * @throws InterruptedException
     */
    public static void main(String[] args) {

        if (args.length == 0) {
            System.err.println("Error: Configuration file path is required as the first argument.");
            return;
        }

        String configFilePath = args[0];
        Path configFileDir = Paths.get(configFilePath).getParent();

        try (FileReader mainReader = new FileReader(configFilePath)) {
            Gson gson = new Gson();
            ConfigFile config = gson.fromJson(mainReader, ConfigFile.class);
            FusionSlamService FusionSlamService = new FusionSlamService(FusionSlam.getInstance());

            // Initialize services
            List<CameraService> camerasServices = initializeCameras(config, gson,configFileDir);
            List<LiDarService> liDarServices = initializeLidars(config, gson, camerasServices.size(),configFileDir);
            PoseService poseService = initializePoseService(config, gson, camerasServices.size(), liDarServices.size(),configFileDir);
            FusionSlam.getInstance().setMicroserviceCount(camerasServices.size() + 1 + liDarServices.size());
            FusionSlam.getInstance().setOutputFilePath(configFileDir.toString());

            // Start microServices in separate threads
            List<Thread> microServices = new LinkedList<>();
            for (CameraService cameraService : camerasServices) {
                microServices.add(new Thread(cameraService, "Camera"));
            }
            for (LiDarService lidarService : liDarServices) {
                microServices.add(new Thread(lidarService, "LiDar"));
            }
            if (poseService != null) {
                microServices.add(new Thread(poseService, "Pose"));
            }
            microServices.add(new Thread(FusionSlamService, "FusionSlam"));
            for (Thread microService : microServices) {
                microService.start();
            }

            // need to create time service after all threads are running
            Thread timeServiceThread = new Thread(new TimeService(config.getTickTime(), config.getDuration()), "Time");

            // Delay to ensure all threads are initialized before starting the time service
            try {
                Thread.sleep(300);
                timeServiceThread.start();
            } catch (InterruptedException ie) {
                System.err.println("Simulation was stopped");
            }
        } catch (IOException e) {
            System.err.println("Failed to load configuration file: " + e.getMessage());
        }
    }

    /**
     * Initializes camera services based on the configuration file.
     *
     * @param config The configuration file object.
     * @param gson   Gson instance for JSON parsing.
     * @return A list of CameraService instances.
     */
    public static List<CameraService> initializeCameras(ConfigFile config, Gson gson,Path configFileDir) {
        List<CameraService> camerasServices = new LinkedList<>();
        if (config.getCameras() != null) {
            String cameraPath = config.getCameras().getCameraDataPath();
            if (cameraPath.startsWith("./")) {
                cameraPath = cameraPath.substring(2);
            }
            List<CamerasConfigurations> Cameras = config.getCameras().getAllCameras();
            Path cameraDataPath = configFileDir.resolve(cameraPath);
            try (FileReader reader = new FileReader(cameraDataPath.toString())) {
                Type mapType = new TypeToken<Map<String, List<StampedDetectedObjects>>>() {
                }.getType();
                Map<String, List<StampedDetectedObjects>> cameraData = gson.fromJson(reader, mapType);
                for (CamerasConfigurations cameraInfo : Cameras) {
                    for (String camera : cameraData.keySet()) {
                        // This is the dectecdObjects list for the specified camera
                        if (camera.equals(cameraInfo.getCameraKey())) {
                            Camera newCamera = new Camera(cameraInfo.getId(), cameraInfo.getFrequency(),
                                    cameraData.get(camera));
                            camerasServices.add(new CameraService(newCamera));
                        }
                    }
                }
            } catch (IOException e) {
                System.err.println("Failed to load Camera's Data: " + e.getMessage());
            }
        }
        return camerasServices;
    }

    /**
     * Initializes LiDAR services based on the configuration file.
     *
     * @param config       The configuration file object.
     * @param gson         Gson instance for JSON parsing.
     * @param numOfCameras Number of cameras in the system.
     * @return A list of LiDarService instances.
     */
    public static List<LiDarService> initializeLidars(ConfigFile config, Gson gson, int numOfCameras,Path configFileDir) {
        List<LiDarService> liDarServices = new LinkedList<>();
        if (config.getLidars() != null) {
            List<LidarConfigurations> allLidar = config.getLidars().getLidars();
            String lidarPath = config.getLidars().getLidarDataPath();
            if (lidarPath.startsWith("./")) {
                lidarPath = lidarPath.substring(2);
            }
            Path LidarDataPath = configFileDir.resolve(lidarPath);
            for (LidarConfigurations lidar : allLidar) {
                LiDarWorkerTracker newLidar = new LiDarWorkerTracker(lidar.getId(), lidar.getFrequency(),
                        LidarDataPath.toString(), numOfCameras);
                liDarServices.add(new LiDarService(newLidar));
            }
        }
        return liDarServices;
    }

    /**
     * Initializes the PoseService based on the configuration file.
     *
     * @param config       The configuration file object.
     * @param gson         Gson instance for JSON parsing.
     * @param numOfCameras Number of cameras in the system.
     * @param numOfLidars  Number of LiDARs in the system.
     * @param configFileDir Representing the path location of the project's folder
     * @return The PoseService instance or null if initialization fails.
     */
    public static PoseService initializePoseService(ConfigFile config, Gson gson, int numOfCameras, int numOfLidars,Path configFileDir) {
        String poseFilePath = config.getPoseFilePath();
        if (poseFilePath != null) {
            if (poseFilePath.startsWith("./")) {
                poseFilePath = poseFilePath.substring(2);
            }
            Path resolvedPosePath = configFileDir.resolve(poseFilePath);
            try (FileReader reader = new FileReader(resolvedPosePath.toString())) {
                Type listType = new TypeToken<List<Pose>>() {
                }.getType();
                List<Pose> poses = gson.fromJson(reader, listType);
                GPSIMU gps = new GPSIMU(0, poses, numOfCameras, numOfLidars);
                return new PoseService(gps);
            } catch (IOException e) {
                System.err.println("Failed to load Poses's Data: " + e.getMessage());
            }
        }
        return null;
    }
}
