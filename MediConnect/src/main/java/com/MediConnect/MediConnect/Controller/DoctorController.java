package com.MediConnect.MediConnect.Controller;

import com.MediConnect.MediConnect.DTO.DoctorResponseDTO;
import com.MediConnect.MediConnect.Model.Doctor;
import com.MediConnect.MediConnect.Service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/doctor")
public class DoctorController {

    @Autowired
    private DoctorService doctorService;

        @PostMapping("/login")
        public ResponseEntity<Map<String, Object>> loginDoctor(@RequestBody Map<String, String> request) {
            try {
                String token = doctorService.loginDoctor(request);
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("token", token);
                return ResponseEntity.ok(response);
            } catch (Exception e) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", e.getMessage());
                return ResponseEntity.badRequest().body(response);
            }
        }

    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getDoctorList() {
        try {
            List<DoctorResponseDTO> doctors = doctorService.getDoctorList();
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("doctors", doctors);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }



}