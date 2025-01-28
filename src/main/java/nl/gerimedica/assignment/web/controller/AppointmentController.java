package nl.gerimedica.assignment.web.controller;

import lombok.RequiredArgsConstructor;
import nl.gerimedica.assignment.service.AppointmentService;
import nl.gerimedica.assignment.util.HospitalUtils;
import nl.gerimedica.assignment.web.model.AppointmentRequest;
import nl.gerimedica.assignment.web.model.AppointmentResponse;
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
    public ResponseEntity<List<AppointmentResponse>> createBulkAppointments(@RequestBody AppointmentRequest payload) {
        HospitalUtils.recordUsage("Controller triggered bulk appointments creation");

        List<AppointmentResponse> created = appointmentService.bulkCreateAppointments(payload);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/appointments-by-reason")
    public ResponseEntity<List<AppointmentResponse>> getAppointmentsByReason(@RequestParam String keyword) {
        List<AppointmentResponse> found = appointmentService.getAppointmentsByReason(keyword);
        return new ResponseEntity<>(found, HttpStatus.OK);
    }

    @DeleteMapping("/delete-appointments")
    public ResponseEntity<String> deleteAppointmentsBySSN(@RequestParam String ssn) {
        appointmentService.deleteAppointmentsBySSN(ssn);
        return new ResponseEntity<>("Deleted all appointments for SSN: " + ssn, HttpStatus.OK);
    }

    @GetMapping("/appointments/latest")
    public ResponseEntity<AppointmentResponse> getLatestAppointment(@RequestParam String ssn) {
        AppointmentResponse latest = appointmentService.findLatestAppointmentBySSN(ssn);
        return new ResponseEntity<>(latest, HttpStatus.OK);
    }
}
