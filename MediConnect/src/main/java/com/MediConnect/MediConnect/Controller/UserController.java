package com.MediConnect.MediConnect.Controller;

import com.MediConnect.MediConnect.DTO.AppointmentRequestDTO;
import com.MediConnect.MediConnect.DTO.AppointmentResponseDTO;
import com.MediConnect.MediConnect.DTO.UserProfileResponseDTO;
import com.MediConnect.MediConnect.DTO.UserUpdateProfileRequestDTO;
import com.MediConnect.MediConnect.Model.User;
import com.MediConnect.MediConnect.Service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    //User register
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerUser(@RequestBody Map<String, String> request) {
        try {
            String name = request.get("name");
            String email = request.get("email");
            String password = request.get("password");
            String token = userService.registerUser(name, email, password);
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


    //User login
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> loginUser(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            String password = request.get("password");
            String token = userService.loginUser(email, password);
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

    //User get profile
    @GetMapping("/get-profile")
    public ResponseEntity<Map<String, Object>> getProfile(HttpServletRequest request) {
        try {
            String userId = (String) request.getAttribute("userId");
            UserProfileResponseDTO userData = userService.getProfile(userId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("userData", userData);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    //User update profile
    @PostMapping("/update-profile")
    public ResponseEntity<Map<String, Object>> updateProfile(
            HttpServletRequest request,
            @RequestParam("name") String name,
            @RequestParam("phone") String phone,
            @RequestParam("address") String address,
            @RequestParam("dob") String dob,
            @RequestParam("gender") String gender,
            @RequestParam(value = "image", required = false) MultipartFile image) {
        try {
            String userId = (String) request.getAttribute("userId");
            UserUpdateProfileRequestDTO requestDTO = new UserUpdateProfileRequestDTO();
            requestDTO.setName(name);
            requestDTO.setPhone(phone);
            requestDTO.setAddress(address);
            requestDTO.setDob(dob);
            requestDTO.setGender(gender);
            userService.updateProfile(userId, requestDTO, image);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Profile Updated");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    //User book appointment
    @PostMapping("/book-appointment")
    public ResponseEntity<Map<String, Object>> bookAppointment(
            HttpServletRequest request,
            @RequestBody AppointmentRequestDTO appointmentRequest) {
        try {
            String userId = (String) request.getAttribute("userId");
            userService.bookAppointment(userId, appointmentRequest);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Appointment Book");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }


    //user get appointment list
    @GetMapping("/appointments")
    public ResponseEntity<Map<String, Object>> getUserAppointments(HttpServletRequest request) {
        try {
            String userId = (String) request.getAttribute("userId");
            List<AppointmentResponseDTO> appointments = userService.getUserAppointments(userId);
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


    //User cancel the appointment
    @PostMapping("/cancel-appointment")
    public ResponseEntity<Map<String, Object>> cancelAppointment(
            HttpServletRequest request,
            @RequestBody Map<String, String> requestBody) {
        try {
            String userId = (String) request.getAttribute("userId");
            String appointmentId = requestBody.get("appointmentId");
            if (appointmentId == null || appointmentId.isBlank()) {
                throw new IllegalArgumentException("Appointment ID is required");
            }
            userService.cancelAppointment(userId, appointmentId);
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
}

