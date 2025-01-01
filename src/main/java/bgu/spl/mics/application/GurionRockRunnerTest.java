package bgu.spl.mics.application;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import com.google.gson.Gson;

import bgu.spl.mics.application.objects.ConfigFile;
import bgu.spl.mics.application.objects.FusionSlam;
import bgu.spl.mics.application.services.CameraService;
import bgu.spl.mics.application.services.FusionSlamService;
import bgu.spl.mics.application.services.LiDarService;
import bgu.spl.mics.application.services.PoseService;
import bgu.spl.mics.application.services.TimeService;
        

public class GurionRockRunnerTest {

    public static void main(String[] args) {
        // Replace with the path to your configuration file
        String configFilePath = "C:\\Users\\n3seg\\OneDrive\\Desktop\\GitHub\\Assignment2\\example input";

        System.out.println("Testing GurionRockRunner...");

        // Step 1: Test the configuration file parsing
        testConfigurationFile(configFilePath);

        // Step 2: Test the initialization of all components and threads
        testSimulationStartup(configFilePath);

        System.out.println("Tests completed.");
    }

    private static void testConfigurationFile(String configFilePath) {
        System.out.println("\nTesting Configuration File Parsing...");
        try (FileReader reader = new FileReader(configFilePath)) {
            Gson gson = new Gson();
            ConfigFile config = gson.fromJson(reader, ConfigFile.class);

            // Validate config
            if (config == null) {
                System.err.println("FAIL: Configuration file is null.");
                return;
            }

            if (config.getTickTime() <= 0) {
                System.err.println("FAIL: Tick time must be greater than 0.");
            } else {
                System.out.println("PASS: Tick time is valid.");
            }

            if (config.getDuration() <= 0) {
                System.err.println("FAIL: Duration must be greater than 0.");
            } else {
                System.out.println("PASS: Duration is valid.");
            }

            if (config.getCameras() == null || config.getCameras().getAllCameras().isEmpty()) {
                System.err.println("FAIL: Cameras configuration is missing or empty.");
            } else {
                System.out.println("PASS: Cameras configuration is valid.");
            }

            if (config.getLidars() == null || config.getLidars().getLidars().isEmpty()) {
                System.err.println("FAIL: LiDARs configuration is missing or empty.");
            } else {
                System.out.println("PASS: LiDARs configuration is valid.");
            }

            if (config.getPoseFilePath() == null || config.getPoseFilePath().isEmpty()) {
                System.err.println("FAIL: Pose file path is missing.");
            } else {
                System.out.println("PASS: Pose file path is valid.");
            }
        } catch (IOException e) {
            System.err.println("FAIL: Error while parsing configuration file: " + e.getMessage());
        }
    }

    private static void testSimulationStartup(String configFilePath) {
        System.out.println("\nTesting Simulation Startup...");

        try (FileReader mainReader = new FileReader(configFilePath)) {
            Gson gson = new Gson();
            ConfigFile config = gson.fromJson(mainReader, ConfigFile.class);

            System.out.println("Initializing simulation components...");
            FusionSlamService fusionSlamService = new FusionSlamService(FusionSlam.getInstance());

            // Initialize cameras
            List<CameraService> camerasServices = GurionRockRunner.initializeCameras(config, gson);

            // Initialize LiDARs
            List<LiDarService> liDarServices = GurionRockRunner.initializeLidars(config, gson);

            // Initialize PoseService
            PoseService poseService = GurionRockRunner.initializePoseService(config, gson);

            // Ensure components are initialized
            if (camerasServices.isEmpty()) {
                System.err.println("FAIL: No camera services initialized.");
            } else {
                System.out.println("PASS: Camera services initialized.");
            }

            if (liDarServices.isEmpty()) {
                System.err.println("FAIL: No LiDAR services initialized.");
            } else {
                System.out.println("PASS: LiDAR services initialized.");
            }

            if (poseService == null) {
                System.err.println("PASS: PoseService is optional, and none was initialized.");
            } else {
                System.out.println("PASS: PoseService initialized.");
            }

            // Simulate thread creation
            List<Thread> microServices = new LinkedList<>();
            for (CameraService cameraService : camerasServices) {
                microServices.add(new Thread(cameraService));
            }
            for (LiDarService lidarService : liDarServices) {
                microServices.add(new Thread(lidarService));
            }
            if (poseService != null) {
                microServices.add(new Thread(poseService));
            }
            microServices.add(new Thread(fusionSlamService));

            for (Thread microService : microServices) {
                microService.start();
            }

            // Start the time service
            System.out.println("Starting TimeService...");
            Thread timeServiceThread = new Thread(new TimeService(config.getTickTime(), config.getDuration()));
            timeServiceThread.start();

            // Wait for a brief moment to ensure threads are running
            Thread.sleep(1000);

            // Verify if threads are alive
            boolean allAlive = true;
            for (Thread microService : microServices) {
                if (!microService.isAlive()) {
                    System.err.println("FAIL: A microservice thread is not running: " + microService.getName());
                    allAlive = false;
                }
            }
            if (allAlive) {
                System.out.println("PASS: All microservices are running.");
            }

            if (!timeServiceThread.isAlive()) {
                System.err.println("FAIL: TimeService thread is not running.");
            } else {
                System.out.println("PASS: TimeService thread is running.");
            }

        } catch (Exception e) {
            System.err.println("FAIL: Simulation startup failed: " + e.getMessage());
        }
    }
}
    