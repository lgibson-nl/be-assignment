package nl.gerimedica.assignment.web.controller;

import lombok.RequiredArgsConstructor;
import nl.gerimedica.assignment.model.Appointment;
import nl.gerimedica.assignment.service.AppointmentService;
import nl.gerimedica.assignment.util.HospitalUtils;
import nl.gerimedica.assignment.web.model.AppointmentDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping("/bulk-appointments")
    public ResponseEntity<List<Appointment>> createBulkAppointments(
            @RequestParam String patientName,
            @RequestParam String ssn,
            @RequestBody List<AppointmentDto> payload
    ) {
        HospitalUtils.recordUsage("Controller triggered bulk appointments creation");

        List<Appointment> created = appointmentService.bulkCreateAppointments(patientName, ssn, payload);
        return new ResponseEntity<>(created, HttpStatus.OK);
    }

    @GetMapping("/appointments-by-reason")
    public ResponseEntity<List<Appointment>> getAppointmentsByReason(@RequestParam String keyword) {
        List<Appointment> found = appointmentService.getAppointmentsByReason(keyword);
        return new ResponseEntity<>(found, HttpStatus.OK);
    }

    @DeleteMapping("/delete-appointments")
    public ResponseEntity<String> deleteAppointmentsBySSN(@RequestParam String ssn) {
        appointmentService.deleteAppointmentsBySSN(ssn);
        return new ResponseEntity<>("Deleted all appointments for SSN: " + ssn, HttpStatus.OK);
    }

    @GetMapping("/appointments/latest")
    public ResponseEntity<Appointment> getLatestAppointment(@RequestParam String ssn) {
        Appointment latest = appointmentService.findLatestAppointmentBySSN(ssn);
        return new ResponseEntity<>(latest, HttpStatus.OK);
    }
}
