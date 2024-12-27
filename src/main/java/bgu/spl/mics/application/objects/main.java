package bgu.spl.mics.application.objects;

import java.util.List;

public class main{
    public static void main(String[] args) {
        // Define the path to the input file
        String filePath = "C:\\Users\\n3seg\\OneDrive\\Desktop\\GitHub\\Assignment2\\example input\\lidar_data.json";

        // Get the singleton instance of LiDarDataBase
        LiDarDataBase database = LiDarDataBase.getInstance(filePath);

        // Retrieve the tracked objects
        List<TrackedObject> trackedObjects = database.getTrackedObjects();

        // Check if the data was loaded successfully
        if (trackedObjects == null || trackedObjects.isEmpty()) {
            System.out.println("No tracked objects found or failed to load data.");
        } else {
            System.out.println("Tracked Objects:");
            for (TrackedObject obj : trackedObjects) {
                System.out.println(obj.toString());
            }
        }
    }
}
