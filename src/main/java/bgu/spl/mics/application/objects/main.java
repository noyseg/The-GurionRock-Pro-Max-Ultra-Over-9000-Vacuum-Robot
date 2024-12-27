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
        String filePath = "C:\\Users\\n3seg\\OneDrive\\Desktop\\GitHub\\Assignment2\\example_input_2\\lidar_data.json";

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
    }
}