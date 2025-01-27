package nl.gerimedica.assignment.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Objects;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String reason;

    private LocalDate date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id")
    private Patient patient;

    public Appointment(String reason, LocalDate date, Patient patient) {
        this.reason = reason;
        this.date = date;
        this.patient = patient;
    }

    /**
     * For a JPA database entity, the equals() method should typically compare only the unique fields, such as the
     * primary key (id). This ensures that the equality check is consistent and efficient.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Appointment otherAppointment)) return false;

        return id != null && id.equals(otherAppointment.id);
    }

    /**
     * For a JPA database entity, the hashCode() method should typically compare only the unique fields, such as the
     * primary key (id). This ensures that the equality check is consistent and efficient.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
