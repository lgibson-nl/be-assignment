package nl.gerimedica.assignment.web.model;

import nl.gerimedica.assignment.model.Appointment;

import java.time.LocalDate;

public record AppointmentResponse(Long id, String reason, LocalDate date, PatientDto patient) {

    public static AppointmentResponse from(Appointment appointment) {
        return new AppointmentResponse(
                appointment.getId(),
                appointment.getReason(),
                appointment.getDate(),
                PatientDto.from(appointment.getPatient())
        );
    }
}
