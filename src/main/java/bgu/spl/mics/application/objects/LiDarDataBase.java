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
    // Singleton instance holder
    private static class DataBaseHolder{
        private static LiDarDataBase Instance = new LiDarDataBase(); 
    }
    private List<StampedCloudPoints> stampedCloudPoints;
    private final List<List<StampedCloudPoints>> stampedCloudPointsSort;

    /**
     * Private constructor to enforce the Singleton pattern.
     */
    private LiDarDataBase() {
        this.stampedCloudPoints = new LinkedList<StampedCloudPoints>();
        this.stampedCloudPointsSort = new LinkedList<>();
    }

    /**
    * Returns the singleton instance of LiDarDataBase.
    *
    * @param filePath The path to the LiDAR data file.
    * @return The singleton instance of LiDarDataBase.
    */
    public static LiDarDataBase getInstance(String filePath) {
        LiDarDataBase instance = DataBaseHolder.Instance;
        if (instance.stampedCloudPoints.isEmpty()) { // First creating of LiDarDataBase
            instance.loadData(filePath); 
            if (!instance.stampedCloudPoints.isEmpty()){
                for(StampedCloudPoints scp: instance.stampedCloudPoints){
                    // Loading LiDAR data from a JSON file was success
                    if (instance.stampedCloudPointsSort.size() != 0){
                        // Creating a list of List<StampedCloudPoints> of sort data:
                        // StampedCloudPoints with identical time will be in same List<StampedCloudPoints> 
                        List<StampedCloudPoints> lastStamped = instance.stampedCloudPointsSort.get(instance.stampedCloudPointsSort.size()-1);
                        // There is a list with this time
                        if (lastStamped.get(0).getTime() == scp.getTime()){
                            lastStamped.add(scp);
                        }
                        // New List<StampedCloudPoints> of this time 
                        else{
                            List<StampedCloudPoints> newLastStamped = new LinkedList<>();
                            newLastStamped.add(scp);
                            instance.stampedCloudPointsSort.add(newLastStamped);
                        }
                    }
                    else{
                        List<StampedCloudPoints> newLastStamped = new LinkedList<>();
                        newLastStamped.add(scp);
                        instance.stampedCloudPointsSort.add(newLastStamped);
                    }
                }
            }
        }
        return instance;
    }

    /**
    * Loads LiDAR data from a JSON file at the specified file path and stores it into the `stampedCloudPoints` list.
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

     /**
     * Returns the list of sorted stamped cloud points by time.
     *
     * @return A sorted list of stamped cloud points grouped by time.
     */
    public List<List<StampedCloudPoints>> getStampedCloudPointsSort() {
        return stampedCloudPointsSort;
    }

    /**
     * Retrieves the cloud points data for a given time and LiDAR ID.
     *
     * @param time The timestamp of the cloud points.
     * @param id   The unique ID of the LiDAR.
     * @return A list of cloud points associated with the given time and LiDAR ID, or null if not found.
     */
    public List<List<Double>> getCloudPointsData(int time,String id){
        for (List<StampedCloudPoints> stmpList: stampedCloudPointsSort){
            if(stmpList.get(0).getTime() == time){
                for(StampedCloudPoints stmCp: stmpList){
                    if (stmCp.getId().equals(id)){
                        return stmCp.getCloudPoints();
                    }

                }
            }
        }
        return null;
    }

    /**
     * Checks if there is an error in the LiDAR data at a specific time.
     *
     * @param time The timestamp to check for LiDAR errors.
     * @return true if there is an error in the data at the specified time, false otherwise.
     */
    public boolean lidarErrorInTime(int time){
        for (List<StampedCloudPoints> stm: this.stampedCloudPointsSort){
            if (stm.get(0).getTime() == time){
                for (StampedCloudPoints stmPoint : stm){
                    if (stmPoint.getId().equals("ERROR")){ 
                        return true;
                    }
                }
            }
            if(stm.get(0).getTime() > time){
                return false;
            }
        }
        return false;
    }
}



