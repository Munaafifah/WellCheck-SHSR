package com.SmartHealthRemoteSystem.SHSR.Service;

import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class DiseaseDescriptionService {

    private final Map<String, String> diseaseDescriptionMap = new HashMap<>();

    @PostConstruct
    public void loadDiseaseDescriptions() {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("symptom_Description.csv"))
        ))) {
            String line;
            br.readLine(); // Skip header

            while ((line = br.readLine()) != null) {
                String[] tokens = line.split(",", 2);
                if (tokens.length >= 2) {
                    String disease = tokens[0].trim().toLowerCase();  // normalize to lowercase
                    String description = tokens[1].trim();
                    diseaseDescriptionMap.put(disease, description);
                }
            }
            System.out.println("✅ Disease descriptions loaded: " + diseaseDescriptionMap.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getDescription(String disease) {
        return diseaseDescriptionMap.getOrDefault(
                disease.toLowerCase(),  // also normalize here
                "No description available."
        );
    }
}
