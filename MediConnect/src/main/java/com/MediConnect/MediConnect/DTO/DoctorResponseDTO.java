package com.MediConnect.MediConnect.DTO;

import lombok.Data;

import java.util.Map;

@Data
public class DoctorResponseDTO {
    private String id;
    private String name;
    private String image;
    private String speciality;
    private String degree;
    private String experience;
    private String about;
    private Double fees;
    private Map<String, Object> address;
    private Boolean available;
    private Long date;
    private Map<String, Object> slotsBooked;
}
