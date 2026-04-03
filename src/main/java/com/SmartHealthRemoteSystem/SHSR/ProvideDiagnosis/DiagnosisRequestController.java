
//MongoDB//
package com.SmartHealthRemoteSystem.SHSR.ProvideDiagnosis;

import com.SmartHealthRemoteSystem.SHSR.Service.DiagnosisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/diagnosis")
public class DiagnosisRequestController {

    private final DiagnosisService diagnosisService;

    @Autowired
    public DiagnosisRequestController(DiagnosisService diagnosisService) {
        this.diagnosisService = diagnosisService;
    }

    @PostMapping("/add")
    public ResponseEntity<String> addDiagnosis(@RequestParam String patientId, @RequestBody Diagnosis diagnosis) {
        String result = diagnosisService.saveDiagnosis(patientId, diagnosis);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Diagnosis>> getAllDiagnoses(@RequestParam String patientId) {
        return ResponseEntity.ok(diagnosisService.getAllDiagnoses(patientId));
    }

    @GetMapping("/{patientId}/{diagnosisId}")
    public ResponseEntity<Diagnosis> getDiagnosis(@PathVariable String patientId, @PathVariable String diagnosisId) {
        Diagnosis diagnosis = diagnosisService.getDiagnosis(patientId, diagnosisId);
        return diagnosis != null ? ResponseEntity.ok(diagnosis) : ResponseEntity.notFound().build();
    }

    @PutMapping("/update")
    public ResponseEntity<String> updateDiagnosis(@RequestParam String patientId, @RequestBody Diagnosis updatedDiagnosis) {
        String result = diagnosisService.updateDiagnosis(patientId, updatedDiagnosis);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteDiagnosis(@RequestParam String patientId, @RequestParam String diagnosisId) {
        return ResponseEntity.ok(diagnosisService.deleteDiagnosis(patientId, diagnosisId));
    }
}
