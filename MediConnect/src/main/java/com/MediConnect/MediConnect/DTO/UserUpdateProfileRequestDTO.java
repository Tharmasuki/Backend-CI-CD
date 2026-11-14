package com.MediConnect.MediConnect.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserUpdateProfileRequestDTO {
    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Phone is required")
    private String phone;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "DOB is required")
    private String dob;

    @NotBlank(message = "Gender is required")
    private String gender;
}
