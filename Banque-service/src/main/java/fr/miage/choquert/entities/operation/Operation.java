package fr.miage.choquert.entities.operation;

import fr.miage.choquert.entities.account.Account;
import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Operation implements Serializable {

    @Serial
    private static final long serialVersionUID = 712413359181L;

    @Id
    private String operationId;
    private Instant datePerformed;
    private String libelle;
    private double montant;
    private String ibanCrediteur;
    private String categorie;
    private String country;
    private boolean virement;

    @ManyToOne
    private Account account;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Operation operation = (Operation) o;
        return operationId != null && Objects.equals(operationId, operation.operationId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
