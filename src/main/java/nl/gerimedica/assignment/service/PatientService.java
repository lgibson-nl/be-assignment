package nl.gerimedica.assignment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.gerimedica.assignment.data.PatientRepository;
import nl.gerimedica.assignment.model.Patient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PatientService {

    private final PatientRepository patientRepo;

    public Patient findPatientBySSN(String ssn) {
        return patientRepo.findBySsn(ssn).orElse(null);
    }

    @Transactional
    public Patient createPatient(String name, String ssn) {
        log.info("Creating new patient with SSN: {}", ssn);
        final Patient createdPatient = new Patient(name, ssn);
        return patientRepo.save(createdPatient);
    }
}
