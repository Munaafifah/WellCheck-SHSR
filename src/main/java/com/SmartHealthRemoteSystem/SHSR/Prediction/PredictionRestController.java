package com.SmartHealthRemoteSystem.SHSR.Prediction;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;          // ✅ correct import
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
    private final RestTemplate         restTemplate;

    @Value("${ml.api.url}")            // value comes from properties
    private String mlApiUrl;

    @Autowired
    public PredictionRestController(SymptomWeightService weightService,
                                    RestTemplate restTemplate) {
        this.weightService = weightService;
        this.restTemplate  = restTemplate;
    }

    /* ---------------------------------------------------------- */
    @PostMapping("/apicall")
    public ResponseEntity<String> callDjangoAPI(@RequestParam("symptom[]") List<String> symptoms) {

        // 1️⃣ convert symptoms ➞ numeric weights
        List<Integer> weights = symptoms.stream()
                                        .map(weightService::getSymptomWeight)
                                        .collect(Collectors.toList());

        Map<String, List<Integer>> body = Map.of("symptoms", weights);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 2️⃣ POST to Django / FastAPI endpoint
        ResponseEntity<String> resp = restTemplate.postForEntity(
                mlApiUrl,                            // https://…/status/
                new HttpEntity<>(body, headers),
                String.class);

        return ResponseEntity.status(resp.getStatusCode())
                             .body(resp.getBody());
    }
}
