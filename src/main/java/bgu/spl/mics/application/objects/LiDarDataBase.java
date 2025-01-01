package bgu.spl.mics.application.objects;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * LiDarDataBase is a singleton class responsible for managing LiDAR data.
 * It provides access to cloud point data and other relevant information for tracked objects.
 */
public class LiDarDataBase {
    private static class DataBaseHolder{
        private static LiDarDataBase Instance = new LiDarDataBase(); 
    }
    private List<StampedCloudPoints> stampedCloudPoints;
    private Map<ObjectDataTracker, List<List<Double>>> stampedCloudPointsMap;

    /**
     * Private constructor to enforce the Singleton pattern.
     */
    private LiDarDataBase() {
        this.stampedCloudPoints = new LinkedList<StampedCloudPoints>();
        this.stampedCloudPointsMap = new HashMap<>();
    }

    /**
     * Returns the singleton instance of LiDarDataBase.
     *
     * @param filePath The path to the LiDAR data file.
     * @return The singleton instance of LiDarDataBase.
     */
    public static LiDarDataBase getInstance(String filePath) {
        LiDarDataBase instance = DataBaseHolder.Instance;
        if (instance.stampedCloudPoints.isEmpty()) { // Data is only read once
            instance.loadData(filePath);
            if (!instance.stampedCloudPoints.isEmpty()){
                for(StampedCloudPoints scp: instance.stampedCloudPoints){
                    ObjectDataTracker key = new ObjectDataTracker(scp.getId(),scp.getTime());
                    instance.stampedCloudPointsMap.put(key,scp.getCloudPoints());
                }
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
            Type listType = new TypeToken<List<StampedCloudPoints>>() {}.getType();
            stampedCloudPoints = gson.fromJson(reader, listType);
        } catch (IOException e) {
            System.err.println("Failed to load LiDAR data: " + e.getMessage());
            stampedCloudPoints = new LinkedList<StampedCloudPoints>();
        }
    }

    /**
     * Returns the list of tracked objects.
     *
     * @return A list of tracked objects, or null if the data is not loaded.
     */
    public List<StampedCloudPoints> getStampedCloudPoints() {
        return stampedCloudPoints;
    }

    public Map<ObjectDataTracker, List<List<Double>>>  getstampedCloudPointsMap() {
        return stampedCloudPointsMap;
    }

    public boolean lidarErrorInTime(int time){
        for (StampedCloudPoints stm: this.stampedCloudPoints){
            if (stm.getTime() == time){
                if (stm.equals("ERROR")){ 
                    return true;
                }
            }
            if (stm.getTime() > time)
                return false;
        }
        return false;
    }

}



