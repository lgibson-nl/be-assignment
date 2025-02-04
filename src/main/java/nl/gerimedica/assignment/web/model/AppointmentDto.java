package nl.gerimedica.assignment.web.model;

import java.time.LocalDate;

public record AppointmentDto(LocalDate date, String reason) {
}
