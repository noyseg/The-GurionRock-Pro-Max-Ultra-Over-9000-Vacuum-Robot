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

import bgu.spl.mics.application.objects.Camera;
import bgu.spl.mics.application.objects.CameraData;
import bgu.spl.mics.application.objects.CamerasConfigurations;
import bgu.spl.mics.application.objects.ConfigFile;
import bgu.spl.mics.application.objects.FusionSlam;
import bgu.spl.mics.application.objects.GPSIMU;
import bgu.spl.mics.application.objects.LiDarWorkerTracker;
import bgu.spl.mics.application.objects.LidarConfigurations;
import bgu.spl.mics.application.objects.Pose;
import bgu.spl.mics.application.objects.STATUS;
import bgu.spl.mics.application.objects.StampedCloudPoints;
import bgu.spl.mics.application.objects.StampedDetectedObjects;
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

    static String configFilePath = "C:\\Users\\n3seg\\OneDrive\\Desktop\\GitHub\\Assignment2\\example input\\configuration_file.json";
    static Path configFileDir = Paths.get(configFilePath).getParent();

    /**
     * The main method of the simulation.
     * This method sets up the necessary components, parses configuration files,
     * initializes services, and starts the simulation.
     *
     * @param args Command-line arguments. The first argument is expected to be the path to the configuration file.
          * @throws InterruptedException 
          */
         public static void main(String[] args) throws InterruptedException {

        // if (args.length == 0) {
        //     System.err.println("Error: Configuration file path is required as the first argument.");
        //     return;
        // }

            try (FileReader mainReader = new FileReader(configFilePath)) {
            Gson gson = new Gson();
            ConfigFile config = gson.fromJson(mainReader, ConfigFile.class);
            System.out.println(config.toString());
            System.out.println("Initializing simulation components...");
            FusionSlamService FusionSlamService = new FusionSlamService(FusionSlam.getInstance());
             // Initialize cameras
            List<CameraService> camerasServices = initializeCameras(config, gson); 
            
            List<LiDarService> liDarServices = initializeLidars(config,gson);

            PoseService poseService = initializePoseService(config, gson);

            FusionSlam.getInstance().setCameraCount(camerasServices.size());
            FusionSlam.getInstance().setMicroserviceCount(camerasServices.size()+1+liDarServices.size());

            List<Thread> microServices = new LinkedList<>();
            for (CameraService cameraService: camerasServices){
                microServices.add(new Thread(cameraService));
            }
            for (LiDarService lidarService: liDarServices){
                microServices.add(new Thread(lidarService));
            }
            if (poseService != null){
                microServices.add(new Thread(poseService));
            }

            microServices.add(new Thread(FusionSlamService));

            for (Thread microService:microServices){
                microService.start();
            }
            
            // need to creat time service after all threads are running 
            System.out.println("Simulation initialized. Starting simulation loop...");
            Thread timeServiceThread = new Thread(new TimeService(config.getTickTime(), config.getDuration()));

            Thread.sleep(300);
            timeServiceThread.start();
            
            // Start the simulation loop or time service (not implemented here)
        } catch (IOException e) {
            System.err.println("Failed to load configuration file: " + e.getMessage());
        }
    }

    public static List<CameraService> initializeCameras(ConfigFile config, Gson gson) {
        List<CameraService> camerasServices = new LinkedList<>(); 
        if (config.getCameras() != null){
            String cameraPath = config.getCameras().getCameraData();
            if (cameraPath.startsWith("./")) {
                cameraPath = cameraPath.substring(2);
            }
            List<CamerasConfigurations> Cameras = config.getCameras().getAllCameras();
            Path cameraDataPath = configFileDir.resolve(cameraPath);
            try (FileReader reader = new FileReader(cameraDataPath.toString())) {
                Type mapType = new TypeToken<Map<String, List<StampedDetectedObjects>>>() {}.getType();
                Map<String, List<StampedDetectedObjects>> cameraData = gson.fromJson(reader, mapType);
                System.out.println("Initializing camera's components...");
                for (String camera: cameraData.keySet()){
                    for (CamerasConfigurations cameraInfo: Cameras){
                        if (camera.equals(cameraInfo.getCameraKey())){
                            Camera newCamera = new Camera(cameraInfo.getId(),cameraInfo.getCameraKey(), cameraInfo.getFrequency(),cameraData.get(camera));
                            camerasServices.add(new CameraService(newCamera));
                            System.out.println(newCamera);
                        }
                    }
                }
            }
            catch (IOException e) {
                System.err.println("Failed to load Camera's Data: " + e.getMessage());
            }
    }
    return camerasServices;
    }

    public static List<LiDarService> initializeLidars(ConfigFile config, Gson gson) {
        List<LiDarService> liDarServices = new LinkedList<>(); 
        if (config.getLidars() != null) {
            System.out.println("Initializing lidar's components...");
            //String lidarPath = config.getLidars().getLidarDataPath();
            List<LidarConfigurations> allLidar = config.getLidars().getLidars();
            String lidarPath = config.getLidars().getLidarDataPath();
            if (lidarPath.startsWith("./")) {
                lidarPath = lidarPath.substring(2);
            }
            Path LidarDataPath = configFileDir.resolve(lidarPath);
            for (LidarConfigurations lidar: allLidar){
                LiDarWorkerTracker newLidar = new LiDarWorkerTracker(lidar.getId(),lidar.getFrequency(), LidarDataPath.toString());
                liDarServices.add(new LiDarService(newLidar));
                System.out.println(newLidar);
            }
        }
        return liDarServices;
    }

    public static PoseService initializePoseService(ConfigFile config, Gson gson) {
        String poseFilePath = config.getPoseFilePath();
        if (poseFilePath != null){
            if (poseFilePath.startsWith("./")) {
                poseFilePath = poseFilePath.substring(2);
            }
                Path resolvedPosePath = configFileDir.resolve(poseFilePath);
            try (FileReader reader = new FileReader(resolvedPosePath.toString())) {
                Type listType = new TypeToken<List<Pose>>() {}.getType();
                List<Pose> poses = gson.fromJson(reader, listType);
                System.out.println("Initializing GPSIMU's components...");
                GPSIMU gps = new GPSIMU(0, poses);
                return new PoseService(gps);
            }
            catch (IOException e) {
                System.err.println("Failed to load Poses's Data: " + e.getMessage());
            }
        }
        return null;
    }
}
