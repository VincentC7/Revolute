package fr.miage.choquert.entities.card;

import fr.miage.choquert.entities.account.Account;
import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Card implements Serializable {

    private static final long serialVersionUID = 497413254891L;

    @Id
    private String cardId;
    private String cardNumber;
    private String code;
    private String cryptogram;
    private double ceiling;
    private boolean blocked;
    private boolean contact;
    private boolean virtual;
    private double longitude;
    private double latitude;

    @ManyToOne
    private Account account;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Card card = (Card) o;
        return cardId != null && Objects.equals(cardId, card.cardId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    public static String randomCardNumber(){
        return randomStringNumber(16);
    }

    public static String randomCrypto(){
        return randomStringNumber(3);
    }

    private static String randomStringNumber(int length){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append((int) (Math.random() * 10));
        }
        return sb.toString();
    }
}
