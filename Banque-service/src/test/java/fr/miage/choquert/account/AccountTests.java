package fr.miage.choquert.account;


import fr.miage.choquert.entities.account.Account;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.Matchers.not;

public class AccountTests {

    @Test
    @DisplayName("Iban char that should works")
    public void testGenerateIban(){
        String iban = Account.randomIBAN();
        assertThat(iban, matchesPattern("FR[0-9]{22}[A-Z][0-9]{2}"));
    }

    @Test
    @DisplayName("Iban char that shouldn't works")
    public void testGenerateWrongIban(){
        String iban = "bad iban";
        assertThat(iban, not(matchesPattern("FR[0-9]{22}[A-Z][0-9]{2}")));
    }

}
