package com.SmartHealthRemoteSystem.SHSR.Prediction;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value; // ✅ correct import
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class PredictionRestController {

    private final RestTemplate restTemplate;

    @Value("${ml.api.url}") // value comes from properties
    private String mlApiUrl;

    public PredictionRestController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /* ---------------------------------------------------------- */
    @PostMapping("/apicall")
    public ResponseEntity<String> callDjangoAPI(@RequestParam("symptom[]") List<String> symptoms) {

        // Send raw symptom names — Django does its own weight lookup internally,
        // so we do NOT convert to numeric weights here anymore.
        System.out.println("📥 Symptoms sent to Django: " + symptoms);

        Map<String, List<String>> body = Map.of("symptoms", symptoms);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // POST to Django endpoint
        ResponseEntity<String> resp = restTemplate.postForEntity(
                mlApiUrl,
                new HttpEntity<>(body, headers),
                String.class);

        return ResponseEntity.status(resp.getStatusCode())
                .body(resp.getBody());
    }
}