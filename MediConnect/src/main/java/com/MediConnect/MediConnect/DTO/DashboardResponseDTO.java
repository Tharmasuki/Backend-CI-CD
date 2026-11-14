package com.MediConnect.MediConnect.DTO;

import lombok.Data;

import java.util.List;

@Data
public class DashboardResponseDTO {
    private long doctors;
    private long appointments;
    private long patients;
    private List<AppointmentResponseDTO> latestAppointments;
}
