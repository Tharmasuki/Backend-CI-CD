package com.MediConnect.MediConnect.Service;

import com.MediConnect.MediConnect.Config.JwtUtil;
import com.MediConnect.MediConnect.DTO.DoctorResponseDTO;
import com.MediConnect.MediConnect.Model.Doctor;
import com.MediConnect.MediConnect.Repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DoctorService {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public String loginDoctor(Map<String, String> request) throws Exception {
        String email = request.get("email");
        String password = request.get("password");

        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            throw new IllegalArgumentException("Email and password are required");
        }

        Doctor doctor = doctorRepository.findByEmail(email);
        if (doctor == null) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        if (!passwordEncoder.matches(password, doctor.getPassword())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        return jwtUtil.generateToken(doctor.getId());
    }

    public void changeAvailability(String docId) throws Exception {
        Doctor doctor = doctorRepository.findById(docId)
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found with ID: " + docId));
        doctor.setAvailable(!doctor.getAvailable());
        doctorRepository.save(doctor);
    }

    public List<DoctorResponseDTO> getDoctorList() {
        return doctorRepository.findAll().stream()
                .map(doctor -> {
                    DoctorResponseDTO dto = new DoctorResponseDTO();
                    dto.setId(doctor.getId());
                    dto.setName(doctor.getName());
                    dto.setImage(doctor.getImage());
                    dto.setSpeciality(doctor.getSpeciality());
                    dto.setDegree(doctor.getDegree());
                    dto.setExperience(doctor.getExperience());
                    dto.setAbout(doctor.getAbout());
                    dto.setFees(doctor.getFees());
                    dto.setAddress(doctor.getAddress());
                    dto.setAvailable(doctor.getAvailable());
                    dto.setDate(doctor.getDate());
                    dto.setSlotsBooked(doctor.getSlotsBooked());
                    return dto;
                })
                .collect(Collectors.toList());
    }

}
