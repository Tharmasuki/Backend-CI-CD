package com.MediConnect.MediConnect.Service;

import com.MediConnect.MediConnect.Config.JwtUtil;
import com.MediConnect.MediConnect.DTO.AppointmentResponseDTO;
import com.MediConnect.MediConnect.DTO.DashboardResponseDTO;
import com.MediConnect.MediConnect.DTO.DoctorResponseDTO;
import com.MediConnect.MediConnect.Model.Appointment;
import com.MediConnect.MediConnect.Model.Doctor;
import com.MediConnect.MediConnect.Repository.AppointmentRepository;
import com.MediConnect.MediConnect.Repository.DoctorRepository;
import com.MediConnect.MediConnect.Repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AdminService {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Value("${admin.email}")
    private String adminEmail;

    @Value("${admin.password}")
    private String adminPassword;


    //API to add doctor
    public Doctor addDoctor(
            String name, String email, String password, String speciality, String degree,
            String experience, String about, Double fees, String addressJson, Boolean available,
            MultipartFile imageFile) throws Exception {
        // Validate required fields
        if (name == null || email == null || password == null || speciality == null ||
                degree == null || experience == null || about == null || fees == null ||
                addressJson == null || imageFile == null || imageFile.isEmpty()) {
            throw new IllegalArgumentException("Missing required fields");
        }

        // Validate email format
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Please enter a valid email");
        }

        // Validate password length
        if (password.length() < 8) {
            throw new IllegalArgumentException("Please enter a strong password (minimum 8 characters)");
        }

        // Check if email already exists
        if (doctorRepository.findByEmail(email) != null) {
            throw new IllegalArgumentException("Email already exists");
        }

        Map<String, Object> address;
        try {
            address = objectMapper.readValue(addressJson, Map.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid JSON format for address: " + e.getMessage());
        }

        // Create Doctor object
        Doctor doctor = new Doctor();
        doctor.setName(name);
        doctor.setEmail(email);
        doctor.setPassword(passwordEncoder.encode(password));
        doctor.setSpeciality(speciality);
        doctor.setDegree(degree);
        doctor.setExperience(experience);
        doctor.setAbout(about);
        doctor.setFees(fees);
        doctor.setAddress(address);
        if (available != null) {
            doctor.setAvailable(available);
        }
        doctor.setDate(System.currentTimeMillis());
        doctor.setSlotsBooked(Map.of());

        // Upload image to Cloudinary
        String imageUrl = cloudinaryService.uploadImage(imageFile);
        doctor.setImage(imageUrl);

        // Save to MongoDB
        return doctorRepository.save(doctor);
    }


    //API to admin login
    public String loginAdmin(String email, String password) throws Exception {
        if (email.equals(adminEmail) && password.equals(adminPassword)) {
            return jwtUtil.generateToken(email + password);
        } else {
            throw new IllegalArgumentException("Invalid credentials");
        }
    }


    //API to get all doctors
    public List<DoctorResponseDTO> getAllDoctors() {
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


    //API for get allappointments
    public List<AppointmentResponseDTO> getAllAppointments() {
        return appointmentRepository.findAll().stream()
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
    public void appointmentCancel(String appointmentId) throws Exception {
        if (appointmentId == null || appointmentId.isBlank()) {
            throw new IllegalArgumentException("Appointment ID is required");
        }

        // Find appointment
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found with ID: " + appointmentId));

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

    //API for get dashboard details
    public DashboardResponseDTO getDashboardData() {
        // Get counts
        long doctorCount = doctorRepository.count();
        long userCount = userRepository.count();
        long appointmentCount = appointmentRepository.count();

        // Get 5 most recent appointments, sorted by date descending
        List<Appointment> latestAppointments = appointmentRepository.findAll(Sort.by(Sort.Direction.DESC, "date"))
                .stream()
                .limit(5)
                .collect(Collectors.toList());

        // Map to AppointmentResponseDTO
        List<AppointmentResponseDTO> latestAppointmentDTOs = latestAppointments.stream()
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

        // Build response DTO
        DashboardResponseDTO dashboardData = new DashboardResponseDTO();
        dashboardData.setDoctors(doctorCount);
        dashboardData.setAppointments(appointmentCount);
        dashboardData.setPatients(userCount);
        dashboardData.setLatestAppointments(latestAppointmentDTOs);

        return dashboardData;
    }
}