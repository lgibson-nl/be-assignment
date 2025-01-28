package nl.gerimedica.assignment.web.model;

import java.util.List;

public record AppointmentRequest(String patientName, String ssn, List<AppointmentDto> appointments) {
}
