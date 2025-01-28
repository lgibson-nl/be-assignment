package nl.gerimedica.assignment;

import nl.gerimedica.assignment.data.AppointmentRepository;
import nl.gerimedica.assignment.web.model.AppointmentDto;
import nl.gerimedica.assignment.web.model.AppointmentRequest;
import nl.gerimedica.assignment.web.model.AppointmentResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class AssignmentApplicationTests {

    private static final String BASE_URL = "http://localhost:8080/api/";
    private static final String PATIENT_NAME = "John Doe";
    private static final String SSN = "123456789";
    private static final String REASON_CHECKUP = "Checkup";
    private static final String REASON_FOLLOW_UP = "Follow-up";

    @Autowired
    private AppointmentRepository appointmentRepository;

    private RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        restTemplate = new RestTemplate();
    }

    @Test
    @DirtiesContext
    void testCreateBulkAppointments() {
        ResponseEntity<AppointmentResponse[]> postResponse = createBulkAppointments();
        List<AppointmentResponse> appointments = List.of(postResponse.getBody());

        assertEquals(HttpStatus.CREATED, postResponse.getStatusCode());
        assertEquals(2, appointments.size());
        assertTrue(appointments.getFirst().reason().contains(REASON_CHECKUP));
        assertTrue(appointments.getLast().reason().contains(REASON_FOLLOW_UP));
    }

    @Test
    @DirtiesContext
    void testGetAppointmentsByReason() {
        createBulkAppointments(); // setup

        String getByReasonUrl = BASE_URL + "appointments-by-reason?keyword=" + REASON_CHECKUP;
        ResponseEntity<AppointmentResponse[]> getResponse = restTemplate.getForEntity(getByReasonUrl, AppointmentResponse[].class);
        List<AppointmentResponse> appointments = List.of(getResponse.getBody());

        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertEquals(1, appointments.size());
        assertTrue(appointments.getFirst().reason().contains(REASON_CHECKUP));
    }

    @Test
    @DirtiesContext
    void testGetLatestAppointment() {
        createBulkAppointments(); // setup

        String getByReasonUrl = BASE_URL + "appointments/latest?ssn=" + SSN;
        ResponseEntity<AppointmentResponse> getResponse = restTemplate.getForEntity(getByReasonUrl, AppointmentResponse.class);
        AppointmentResponse appointment = getResponse.getBody();

        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertTrue(appointment.reason().contains(REASON_FOLLOW_UP));
    }

    @Test
    @DirtiesContext
    void testDeleteAppointments() {
        createBulkAppointments(); // setup
        assertEquals(2, appointmentRepository.findAll().size());

        String deleteUrl = BASE_URL + "delete-appointments?ssn=" + SSN;
        restTemplate.delete(deleteUrl);

        assertEquals(0, appointmentRepository.findAll().size());
    }

    private ResponseEntity<AppointmentResponse[]> createBulkAppointments() {
        String postUrl = BASE_URL + "bulk-appointments";

        var appointments = List.of(
                new AppointmentDto(LocalDate.now(), REASON_CHECKUP),
                new AppointmentDto(LocalDate.now().plusDays(1), REASON_FOLLOW_UP)
        );

        var payload = new AppointmentRequest(PATIENT_NAME, SSN, appointments);

        return restTemplate.postForEntity(postUrl, payload, AppointmentResponse[].class);
    }
}
