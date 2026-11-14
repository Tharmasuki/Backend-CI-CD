package com.MediConnect.MediConnect.Model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Data
@Document(collection = "appointment")
public class Appointment {
    @Id
    private String id;

    @NotBlank(message = "User ID is required")
    private String userId;

    @NotBlank(message = "Doctor ID is required")
    private String docId;

    @NotBlank(message = "Slot date is required")
    private String slotDate;

    @NotBlank(message = "Slot time is required")
    private String slotTime;

    @NotNull(message = "User data is required")
    private Object userData;

    @NotNull(message = "Doctor data is required")
    private Object docData;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private Double amount;

    @NotNull(message = "Date is required")
    private Long date;

    private Boolean cancelled = false;

    private Boolean payment = false;

    private Boolean isCompleted = false;
}