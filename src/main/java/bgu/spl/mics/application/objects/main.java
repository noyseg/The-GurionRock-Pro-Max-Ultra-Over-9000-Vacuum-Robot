package bgu.spl.mics.application.objects;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class main {
    public static void main(String[] args) {

        // Path to the JSON file
        String filePath = "C:\\Users\\n3seg\\OneDrive\\Desktop\\GitHub\\Assignment2\\example input\\lidar_data.json";

        try (FileReader reader = new FileReader(filePath)) {
            Gson gson = new Gson();

            // Define the type for deserialization
            Type listType = new TypeToken<List<StampedCloudPoints>>() {}.getType();

            // Deserialize the JSON file
            List<StampedCloudPoints> stampedCloudPoints = gson.fromJson(reader, listType);

            // Print the loaded data
            if (stampedCloudPoints != null) {
                System.out.println("Tracked Objects:");
                for (StampedCloudPoints stm : stampedCloudPoints) {
                    System.out.println(stm);
                }
            } else {
                System.out.println("No data found in the file.");
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
        //Define a Pose (robot's position and orientation)
        Pose robotPose = new Pose(10.0f, 5.0f, 45.0f, 1); // x = 10, y = 5, yaw = 45 degrees, time = 1
        
        // Define detected object's local coordinates relative to the robot
        float xDetected = 3.0f; // Local x-coordinate
        float yDetected = 4.0f; // Local y-coordinate
        
        // Create an instance of FusionSlam (assumes it's a singleton)
        FusionSlam fusionSlam = FusionSlam.getInstance();
        
        // Perform the pose transformation
        CloudPoint transformedPoint = fusionSlam.poseTranformation(robotPose, xDetected, yDetected);
        
        // Output the result
        System.out.println("Transformed CloudPoint: " + transformedPoint);
    }
}