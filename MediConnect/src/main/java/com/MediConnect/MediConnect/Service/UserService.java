package com.MediConnect.MediConnect.Service;

import com.MediConnect.MediConnect.Config.JwtUtil;
import com.MediConnect.MediConnect.DTO.*;
import com.MediConnect.MediConnect.Model.Appointment;
import com.MediConnect.MediConnect.Model.Doctor;
import com.MediConnect.MediConnect.Model.User;
import com.MediConnect.MediConnect.Repository.AppointmentRepository;
import com.MediConnect.MediConnect.Repository.DoctorRepository;
import com.MediConnect.MediConnect.Repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private ObjectMapper objectMapper;

    //API for register User
    public String registerUser(String name, String email, String password) throws Exception {
        if (name == null || name.isBlank() || email == null || email.isBlank() || password == null || password.isBlank()) {
            throw new IllegalArgumentException("Missing Details");
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("enter a valid email");
        }

        if (password.length() < 8) {
            throw new IllegalArgumentException("enter a strong password");
        }

        if (userRepository.findByEmail(email) != null) {
            throw new IllegalArgumentException("Email already exists");
        }

        String hashedPassword = passwordEncoder.encode(password);

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(hashedPassword);

        User savedUser = userRepository.save(user);

        return jwtUtil.generateToken(savedUser.getId());
    }

    //API for login User
    public String loginUser(String email, String password) throws Exception {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new IllegalArgumentException("User does not exist");
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        return jwtUtil.generateToken(user.getId());
    }

//API for get user profile
public UserProfileResponseDTO getProfile(String userId) throws Exception {
    User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

    // Map to DTO
    UserProfileResponseDTO response = new UserProfileResponseDTO();
    response.setId(user.getId());
    response.setName(user.getName());
    response.setEmail(user.getEmail());
    response.setImage(user.getImage());
    response.setAddress(user.getAddress());
    response.setGender(user.getGender());
    response.setDob(user.getDob());
    response.setPhone(user.getPhone());

    return response;
}


    //API for update profile
    public void updateProfile(String userId, UserUpdateProfileRequestDTO request, MultipartFile image) throws Exception {
        // Validate required fields
        if (request.getName() == null || request.getName().isBlank() ||
                request.getPhone() == null || request.getPhone().isBlank() ||
                request.getDob() == null || request.getDob().isBlank() ||
                request.getGender() == null || request.getGender().isBlank()) {
            throw new IllegalArgumentException("Data missing");
        }

        // Find user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        // Parse address JSON
        Map<String, String> address;
        try {
            address = objectMapper.readValue(request.getAddress(), Map.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid address format");
        }

        // Update fields
        user.setName(request.getName());
        user.setPhone(request.getPhone());
        user.setAddress(address);
        user.setDob(request.getDob());
        user.setGender(request.getGender());

        // Handle image upload if provided
        if (image != null && !image.isEmpty()) {
            String imageUrl = cloudinaryService.uploadImage(image);
            user.setImage(imageUrl);
        }

        // Save updated user
        userRepository.save(user);
    }


//API to book appointment
    public void bookAppointment(String userId, AppointmentRequestDTO request) throws Exception {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));


        Doctor doctor = doctorRepository.findById(request.getDocId())
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found with ID: " + request.getDocId()));

        if (!doctor.getAvailable()) {
            throw new IllegalArgumentException("Doctor not available");
        }

        // Check slot availability
        Map<String, Object> slotsBooked = doctor.getSlotsBooked() != null ? doctor.getSlotsBooked() : new HashMap<>();
        String slotDate = request.getSlotDate();
        String slotTime = request.getSlotTime();

        List<String> bookedSlots = (List<String>) slotsBooked.getOrDefault(slotDate, new ArrayList<>());
        if (bookedSlots.contains(slotTime)) {
            throw new IllegalArgumentException("Slot not available");
        }

        // Add slot to booked slots
        bookedSlots.add(slotTime);
        slotsBooked.put(slotDate, bookedSlots);
        doctor.setSlotsBooked(slotsBooked);
        doctorRepository.save(doctor);

        // Create user data DTO
        UserProfileResponseDTO userData = new UserProfileResponseDTO();
        userData.setId(user.getId());
        userData.setName(user.getName());
        userData.setEmail(user.getEmail());
        userData.setImage(user.getImage());
        userData.setAddress(user.getAddress());
        userData.setGender(user.getGender());
        userData.setDob(user.getDob());
        userData.setPhone(user.getPhone());

        // Create doctor data DTO
        DoctorResponseDTO docData = new DoctorResponseDTO();
        docData.setId(doctor.getId());
        docData.setName(doctor.getName());
        docData.setImage(doctor.getImage());
        docData.setSpeciality(doctor.getSpeciality());
        docData.setDegree(doctor.getDegree());
        docData.setExperience(doctor.getExperience());
        docData.setAbout(doctor.getAbout());
        docData.setFees(doctor.getFees());
        docData.setAddress(doctor.getAddress());
        docData.setAvailable(doctor.getAvailable());
        docData.setDate(doctor.getDate());
        docData.setSlotsBooked(null);

        // Create appointment
        Appointment appointment = new Appointment();
        appointment.setUserId(userId);
        appointment.setDocId(request.getDocId());
        appointment.setSlotDate(slotDate);
        appointment.setSlotTime(slotTime);
        appointment.setUserData(userData);
        appointment.setDocData(docData);
        appointment.setAmount(doctor.getFees());
        appointment.setDate(System.currentTimeMillis());
        appointment.setCancelled(false);
        appointment.setPayment(false);
        appointment.setIsCompleted(false);

        // Save appointment
        appointmentRepository.save(appointment);
    }


//API for get appointment list
    public List<AppointmentResponseDTO> getUserAppointments(String userId) {
        return appointmentRepository.findByUserId(userId).stream()
                .map(appointment -> {
                    AppointmentResponseDTO dto = new AppointmentResponseDTO();
                    dto.setId(appointment.getId());
                    dto.setUserId(appointment.getUserId());
                    dto.setDocId(appointment.getDocId());
                    dto.setSlotDate(appointment.getSlotDate());
                    dto.setSlotTime(appointment.getSlotTime());
                    dto.setUserData(appointment.getUserData());
                    dto.setDocData(appointment.getDocData());
                    dto.setAmount(appointment.getAmount());
                    dto.setDate(appointment.getDate());
                    dto.setCancelled(appointment.getCancelled());
                    dto.setPayment(appointment.getPayment());
                    dto.setIsCompleted(appointment.getIsCompleted());
                    return dto;
                })
                .collect(Collectors.toList());
    }



    //API for cancel appointment
    public void cancelAppointment(String userId, String appointmentId) throws Exception {
        // Validate appointmentId
        if (appointmentId == null || appointmentId.isBlank()) {
            throw new IllegalArgumentException("Appointment ID is required");
        }

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found with ID: " + appointmentId));

        if (!appointment.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Unauthorized action");
        }

        // Update appointment to cancelled
        appointment.setCancelled(true);
        appointmentRepository.save(appointment);

        // Release doctor slot
        Doctor doctor = doctorRepository.findById(appointment.getDocId())
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found with ID: " + appointment.getDocId()));

        Map<String, Object> slotsBooked = doctor.getSlotsBooked() != null ? doctor.getSlotsBooked() : new HashMap<>();
        String slotDate = appointment.getSlotDate();
        String slotTime = appointment.getSlotTime();

        List<String> bookedSlots = (List<String>) slotsBooked.getOrDefault(slotDate, new ArrayList<>());
        bookedSlots.remove(slotTime);
        slotsBooked.put(slotDate, bookedSlots);
        doctor.setSlotsBooked(slotsBooked);
        doctorRepository.save(doctor);
    }
}