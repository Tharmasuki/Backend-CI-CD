package com.MediConnect.MediConnect.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AppointmentRequestDTO {
    @NotBlank(message = "Doctor ID is required")
    private String docId;

    @NotBlank(message = "Slot date is required")
    private String slotDate;

    @NotBlank(message = "Slot time is required")
    private String slotTime;
}