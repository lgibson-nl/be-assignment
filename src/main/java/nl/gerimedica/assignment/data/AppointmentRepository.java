package nl.gerimedica.assignment.data;

import nl.gerimedica.assignment.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Meta;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    public List<Appointment> findByReasonContainingIgnoreCase(String keyword);

    @Modifying
    @Query("delete from Appointment a where a.patient.ssn = :ssn")
    public int deleteAppointmentsByPatient_Ssn(String ssn);

    public List<Appointment> findByPatient_Ssn(String ssn);
}
