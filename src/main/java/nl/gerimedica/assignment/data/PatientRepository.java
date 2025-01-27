package nl.gerimedica.assignment.data;

import nl.gerimedica.assignment.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    public Optional<Patient> findBySsn(String ssn);
}
