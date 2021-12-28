package fr.miage.choquert.entities.account;

import fr.miage.choquert.entities.card.Card;
import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

@Entity
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Account implements Serializable {

    @Serial
    private static final long serialVersionUID = 267433254591L;

    @Id
    private String accountId;
    private String iban;
    private String accountNumber;
    private String name;
    private String surname;
    private String birthday;
    private String country;
    private String passport;
    private String tel;
    private String secret;
    private Double balance;

    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
    @ToString.Exclude
    private Set<Card> cards;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Account account = (Account) o;
        return accountId != null && Objects.equals(accountId, account.accountId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    public static String randomIBAN() {
        StringBuilder sb = new StringBuilder("FR");
        for (int i = 0; i < 22; i++) {
            sb.append((int) (Math.random() * 9));
        }
        sb.append( (char) ('A' + Math.random() * 26) );
        sb.append((int) (Math.random() * 9));
        sb.append((int) (Math.random() * 9));
        return sb.toString();
    }

}
