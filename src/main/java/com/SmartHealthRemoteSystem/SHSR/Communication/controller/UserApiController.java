package com.SmartHealthRemoteSystem.SHSR.Communication.controller;

import com.SmartHealthRemoteSystem.SHSR.User.Doctor.MongoDoctorRepository;
import com.SmartHealthRemoteSystem.SHSR.User.Radiographer.MongoRadiographerRepository;
import com.SmartHealthRemoteSystem.SHSR.User.Radiologist.MongoRadiologistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserApiController {

    private final MongoDoctorRepository doctorRepo;
    private final MongoRadiographerRepository radiographerRepo;
    private final MongoRadiologistRepository radiologistRepo;

    @Autowired
    public UserApiController(MongoDoctorRepository doctorRepo,
                             MongoRadiographerRepository radiographerRepo,
                             MongoRadiologistRepository radiologistRepo) {
        this.doctorRepo       = doctorRepo;
        this.radiographerRepo = radiographerRepo;
        this.radiologistRepo  = radiologistRepo;
    }

    // Returns all Doctors, Radiographers, and Radiologists for the communication participant picker
    @GetMapping("/chat-participants")
    public ResponseEntity<List<Map<String, String>>> getChatParticipants() {
        List<Map<String, String>> result = new ArrayList<>();

        doctorRepo.findAll().forEach(d -> {
            Map<String, String> entry = new LinkedHashMap<>();
            entry.put("userId", d.getUserId());
            entry.put("name",   d.getName());
            entry.put("role",   "Doctor");
            result.add(entry);
        });

        radiographerRepo.findAll().forEach(r -> {
            Map<String, String> entry = new LinkedHashMap<>();
            entry.put("userId", r.getUserId());
            entry.put("name",   r.getName());
            entry.put("role",   "Radiographer");
            result.add(entry);
        });

        radiologistRepo.findAll().forEach(r -> {
            Map<String, String> entry = new LinkedHashMap<>();
            entry.put("userId", r.getUserId());
            entry.put("name",   r.getName());
            entry.put("role",   "Radiologist");
            result.add(entry);
        });

        return ResponseEntity.ok(result);
    }
}
