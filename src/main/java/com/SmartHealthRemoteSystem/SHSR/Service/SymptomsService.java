//MongoDB//
package com.SmartHealthRemoteSystem.SHSR.Service;

import com.SmartHealthRemoteSystem.SHSR.Symptoms.Symptom;
import com.SmartHealthRemoteSystem.SHSR.Symptoms.SymptomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class SymptomsService {

    @Autowired
    private SymptomRepository symptomRepository;

    public List<Symptom> getAllSymptoms() {
        return symptomRepository.findAll();
    }

    public Symptom getSymptomById(String id) {
        return symptomRepository.findById(id).orElse(null);
    }

    public Symptom saveSymptom(Symptom symptom) {
        return symptomRepository.save(symptom);
    }

    public Symptom updateSymptom(Symptom symptom) {
        return symptomRepository.save(symptom);
    }

    public void deleteSymptom(String id) {
        symptomRepository.deleteById(id);
    }


    // 🔧 STEP 1: Add normalizeSymptom function here:
    public String normalizeSymptom(String symptomName) {
        return symptomName.replace("_", " ").toLowerCase().trim();
    }

    public int getSymptomWeight(String symptomName) {
        String normalizedName = normalizeSymptom(symptomName);
        Symptom symptom = symptomRepository.findByNameIgnoreCase(normalizedName);
        if (symptom != null && symptom.getWeight() != null) {
            return symptom.getWeight();
        } else {
            System.out.println("⚠ Symptom not found: " + normalizedName);
            return 0;
        }
    }


}
