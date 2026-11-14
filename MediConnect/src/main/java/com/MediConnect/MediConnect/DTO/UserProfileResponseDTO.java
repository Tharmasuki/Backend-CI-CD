package com.MediConnect.MediConnect.DTO;

import lombok.Data;

import java.util.Map;

@Data
public class UserProfileResponseDTO {
    private String id;
    private String name;
    private String email;
    private String image;
    private Map<String, String> address;
    private String gender;
    private String dob;
    private String phone;
}