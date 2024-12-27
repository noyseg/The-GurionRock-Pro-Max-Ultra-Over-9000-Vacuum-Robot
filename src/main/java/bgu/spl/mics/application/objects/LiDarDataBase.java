package bgu.spl.mics.application.objects;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

/**
 * LiDarDataBase is a singleton class responsible for managing LiDAR data.
 * It provides access to cloud point data and other relevant information for tracked objects.
 */
public class LiDarDataBase {
    private static class DataBaseHolder{
        private static LiDarDataBase Instance = new LiDarDataBase(); 
    }
    private List<TrackedObject> trackedObjects;

    /**
     * Private constructor to enforce the Singleton pattern.
     */
    private LiDarDataBase() {
        this.trackedObjects = new LinkedList<TrackedObject>();
    }

    /**
     * Returns the singleton instance of LiDarDataBase.
     *
     * @param filePath The path to the LiDAR data file.
     * @return The singleton instance of LiDarDataBase.
     */
    public static LiDarDataBase getInstance(String filePath) {
        LiDarDataBase instance = DataBaseHolder.Instance;
        synchronized (instance) {
            if (instance.trackedObjects.isEmpty()) { // Data is only read once
                instance.loadData(filePath);
            }
        }
        return instance;
    }

    /**
     * Loads LiDAR data from a JSON file.
     *
     * @param filePath The path to the JSON file containing the LiDAR data.
     */
    private void loadData(String filePath) {
        try (FileReader reader = new FileReader(filePath)) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<TrackedObject>>() {}.getType();
            trackedObjects = gson.fromJson(reader, listType);
        } catch (IOException e) {
            System.err.println("Failed to load LiDAR data: " + e.getMessage());
            trackedObjects = null;
        }
    }

    /**
     * Returns the list of tracked objects.
     *
     * @return A list of tracked objects, or null if the data is not loaded.
     */
    public List<TrackedObject> getTrackedObjects() {
        return trackedObjects;
    }
}



