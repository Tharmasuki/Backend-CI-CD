package com.MediConnect.MediConnect.Controller;

import com.MediConnect.MediConnect.DTO.AppointmentResponseDTO;
import com.MediConnect.MediConnect.DTO.DashboardResponseDTO;
import com.MediConnect.MediConnect.DTO.DoctorResponseDTO;
import com.MediConnect.MediConnect.Model.Doctor;
import com.MediConnect.MediConnect.Service.AdminService;
import com.MediConnect.MediConnect.Service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private DoctorService doctorService;

    //Admin add doctor
    @PostMapping("/add-doctor")
    public ResponseEntity<Map<String, Object>> addDoctor(
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("speciality") String speciality,
            @RequestParam("degree") String degree,
            @RequestParam("experience") String experience,
            @RequestParam("about") String about,
            @RequestParam("fees") Double fees,
            @RequestParam("address") String address,
            @RequestParam(value = "available", required = false) Boolean available,
            @RequestParam("image") MultipartFile file) {
        try {
            adminService.addDoctor(name, email, password, speciality, degree, experience, about, fees,
                    address, available, file);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Doctor added");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }


    //Admin login
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> loginAdmin(@RequestBody Map<String, String> credentials) {
        try {
            String email = credentials.get("email");
            String password = credentials.get("password");
            String token = adminService.loginAdmin(email, password);
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


    //Admin get all doctors
    @PostMapping("/all-doctors")
    public ResponseEntity<Map<String, Object>> getAllDoctors() {
        try {
            List<DoctorResponseDTO> doctors = adminService.getAllDoctors();
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

    //Admin change availablity
    @PostMapping("/change-availablity")
    public ResponseEntity<Map<String, Object>> changeAvailability(@RequestBody Map<String, String> request) {
        try {
            String docId = request.get("docId");
            if (docId == null || docId.isEmpty()) {
                throw new IllegalArgumentException("Missing docId");
            }
            doctorService.changeAvailability(docId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Availablity changed");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }


    //Admin get all appointments
    @GetMapping("/appointments")
    public ResponseEntity<Map<String, Object>> getAllAppointments() {
        try {
            List<AppointmentResponseDTO> appointments = adminService.getAllAppointments();
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("appointments", appointments);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }


    //Admin cancel appointment
    @PostMapping("/cancel-appointment")
    public ResponseEntity<Map<String, Object>> cancelAppointment(@RequestBody Map<String, String> requestBody) {
        try {
            String appointmentId = requestBody.get("appointmentId");
            if (appointmentId == null || appointmentId.isBlank()) {
                throw new IllegalArgumentException("Appointment ID is required");
            }
            adminService.appointmentCancel(appointmentId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Appointment Cancelled");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    //Admin dashboard details
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard() {
        try {
            DashboardResponseDTO dashData = adminService.getDashboardData();
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("dashData", dashData);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}