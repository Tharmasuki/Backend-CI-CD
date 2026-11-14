package com.MediConnect.MediConnect.DTO;

import lombok.Data;

@Data
public class AppointmentResponseDTO {
    private String id;
    private String userId;
    private String docId;
    private String slotDate;
    private String slotTime;
    private Object userData;
    private Object docData;
    private Double amount;
    private Long date;
    private Boolean cancelled;
    private Boolean payment;
    private Boolean isCompleted;
}
