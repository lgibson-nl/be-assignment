package nl.gerimedica.assignment.web.model;

import nl.gerimedica.assignment.model.Patient;

public record PatientDto(Long id, String name, String ssn) {

    public static PatientDto from(Patient patient) {
        return new PatientDto(
                patient.getId(),
                patient.getName(),
                patient.getSsn()
        );
    }
}
