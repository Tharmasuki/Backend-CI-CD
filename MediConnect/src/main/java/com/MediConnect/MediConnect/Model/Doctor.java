package com.MediConnect.MediConnect.Model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.Map;

@Data
@Document(collection = "doctor")
public class Doctor {
    @Id
    private String id;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Indexed(unique = true)
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    @NotBlank(message = "Image URL is required")
    private String image;

    @NotBlank(message = "Speciality is required")
    private String speciality;

    @NotBlank(message = "Degree is required")
    private String degree;

    @NotBlank(message = "Experience is required")
    private String experience;

    @NotBlank(message = "About is required")
    private String about;

    private Boolean available = true;

    @NotNull(message = "Fees are required")
    @Positive(message = "Fees must be positive")
    private Double fees;

    @NotNull(message = "Address is required")
    private Map<String, Object> address;

    @NotNull(message = "Date is required")
    private Long date;

    private Map<String, Object> slotsBooked;
}
