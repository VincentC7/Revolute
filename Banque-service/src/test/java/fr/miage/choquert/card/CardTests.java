package fr.miage.choquert.card;

import fr.miage.choquert.entities.card.Card;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.matchesPattern;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CardTests {

    @Test
    @DisplayName("Card number generator")
    public void testGenerateCardNumber(){
        String cardNumber = Card.randomCardNumber();
        assertEquals(16, cardNumber.length());
        assertThat(cardNumber, matchesPattern("[0-9]{16}"));
    }


    @Test
    @DisplayName("cryptogram generator")
    public void testGenerateCrypto(){
        String crypto = Card.randomCrypto();
        assertEquals(3, crypto.length());
        assertThat(crypto, matchesPattern("[0-9]{3}"));
    }

}
