package com.SmartHealthRemoteSystem.SHSR.Prediction;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value; // ✅ correct import
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.SmartHealthRemoteSystem.SHSR.Service.SymptomWeightService;

@RestController
public class PredictionRestController {

    private final SymptomWeightService weightService;
    private final RestTemplate restTemplate;

    @Value("${ml.api.url}") // value comes from properties
    private String mlApiUrl;

    @Autowired
    public PredictionRestController(SymptomWeightService weightService,
            RestTemplate restTemplate) {
        this.weightService = weightService;
        this.restTemplate = restTemplate;
    }

    /* ---------------------------------------------------------- */
    @PostMapping("/apicall")
    public ResponseEntity<String> callDjangoAPI(@RequestParam("symptom[]") List<String> symptoms) {

        // ✅ Django does its own symptom-name -> weight lookup internally
        // (see MyAPI/views.py symptom_weight_mapping). Send the raw symptom
        // name strings as-is — do NOT pre-convert to weights here, otherwise
        // Django receives numbers where it expects names and every lookup
        // falls back to 0 regardless of symptom.
        System.out.println("📥 Symptoms sent to Django: " + symptoms);

        Map<String, List<String>> body = Map.of("symptoms", symptoms);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 2️⃣ POST to Django / FastAPI endpoint
        ResponseEntity<String> resp = restTemplate.postForEntity(
                mlApiUrl,
                new HttpEntity<>(body, headers),
                String.class);

        return ResponseEntity.status(resp.getStatusCode())
                .body(resp.getBody());
    }
}