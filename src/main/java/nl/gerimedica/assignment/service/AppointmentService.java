package nl.gerimedica.assignment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.gerimedica.assignment.data.AppointmentRepository;
import nl.gerimedica.assignment.model.Appointment;
import nl.gerimedica.assignment.model.Patient;
import nl.gerimedica.assignment.util.HospitalUtils;
import nl.gerimedica.assignment.web.model.AppointmentDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppointmentService {

    private final PatientService patientService;

    private final AppointmentRepository appointmentRepo;

    @Transactional
    public List<Appointment> bulkCreateAppointments(
            String patientName,
            String ssn,
            List<AppointmentDto> appointmentRequests
    ) {
        Patient patient = patientService.findPatientBySSN(ssn);

        if (patient == null) {
            log.info("No patient found with SSN: {}", ssn);
            patient = patientService.createPatient(patientName, ssn);
        } else {
            log.info("Existing patient found, SSN: {}", patient.getSsn());
        }

        final List<Appointment> createdAppointments = new ArrayList<>();

        for (AppointmentDto request : appointmentRequests) {
            createdAppointments.add(new Appointment(request.reason(), request.date(), patient));
        }

        appointmentRepo.saveAll(createdAppointments);
        log.info("Created appointment(s) for patient with SSN [{}]: {}", ssn, createdAppointments);

        HospitalUtils.recordUsage("Bulk create appointments");

        return createdAppointments;
    }

    public List<Appointment> getAppointmentsByReason(String reasonKeyword) {
        HospitalUtils.recordUsage("Get appointments by reason");
        return appointmentRepo.findByReasonContainingIgnoreCase(reasonKeyword);
    }

    @Transactional
    public void deleteAppointmentsBySSN(String ssn) {
        int deletedRecords = appointmentRepo.deleteAppointmentsByPatient_Ssn(ssn);
        log.info("Deleted {} appointments for patient with SSN: {}", deletedRecords, ssn);
    }

    public Appointment findLatestAppointmentBySSN(String ssn) {
        List<Appointment> appointments = appointmentRepo.findByPatient_Ssn(ssn);

        if (appointments.isEmpty()) {
            return null;
        } else {
            return appointments.stream().max(Comparator.comparing(Appointment::getDate)).get();
        }
    }
}
