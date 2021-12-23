package fr.miage.choquert.entities.account;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Account implements Serializable {

    @Serial
    private static final long serialVersionUID = 267433254591L;

    @Id
    private String id;
    private String iban;
    private String accountNumber;
    private String name;
    private String surname;
    private LocalDate birthday;
    private String country;
    private String passport;
    private String tel;
    private String secret;
    private Double balance;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Account account = (Account) o;
        return id != null && Objects.equals(id, account.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
