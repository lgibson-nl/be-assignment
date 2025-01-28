package nl.gerimedica.assignment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.gerimedica.assignment.data.AppointmentRepository;
import nl.gerimedica.assignment.model.Appointment;
import nl.gerimedica.assignment.model.Patient;
import nl.gerimedica.assignment.util.HospitalUtils;
import nl.gerimedica.assignment.web.model.AppointmentDto;
import nl.gerimedica.assignment.web.model.AppointmentRequest;
import nl.gerimedica.assignment.web.model.AppointmentResponse;
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
    public List<AppointmentResponse> bulkCreateAppointments(AppointmentRequest request) {
        final String ssn = request.ssn();
        Patient patient = patientService.findPatientBySSN(ssn);

        if (patient == null) {
            log.info("No patient found with SSN: {}", ssn);
            patient = patientService.createPatient(request.patientName(), ssn);
        } else {
            log.info("Existing patient found, SSN: {}", ssn);
        }

        final List<Appointment> createdAppointments = new ArrayList<>();

        for (AppointmentDto requestedAppointment : request.appointments()) {
            createdAppointments.add(new Appointment(requestedAppointment.reason(), requestedAppointment.date(), patient));
        }

        appointmentRepo.saveAll(createdAppointments);
        log.info("Created appointment(s) for patient with SSN [{}]: {}", ssn, createdAppointments);

        HospitalUtils.recordUsage("Bulk create appointments");

        return toResponseList(createdAppointments);
    }

    public List<AppointmentResponse> getAppointmentsByReason(String reasonKeyword) {
        HospitalUtils.recordUsage("Get appointments by reason");
        log.debug("Searching appointments by reason: {}", reasonKeyword);

        List<Appointment> appointments = appointmentRepo.findByReasonContainingIgnoreCase(reasonKeyword);
        log.info("Found {} appointments by reason: {}", appointments.size(), reasonKeyword);

        return toResponseList(appointments);
    }

    private List<AppointmentResponse> toResponseList(List<Appointment> appointments) {
        return appointments.stream().map(AppointmentResponse::from).toList();
    }

    @Transactional
    public void deleteAppointmentsBySSN(String ssn) {
        int deletedRecords = appointmentRepo.deleteAppointmentsByPatient_Ssn(ssn);
        log.info("Deleted {} appointments for patient with SSN: {}", deletedRecords, ssn);
    }

    public AppointmentResponse findLatestAppointmentBySSN(String ssn) {
        List<Appointment> appointments = appointmentRepo.findByPatient_Ssn(ssn);

        if (appointments.isEmpty()) {
            return null;
        } else {
            Appointment latestAppointment = appointments.stream().max(Comparator.comparing(Appointment::getDate)).get();
            return AppointmentResponse.from(latestAppointment);
        }
    }
}
