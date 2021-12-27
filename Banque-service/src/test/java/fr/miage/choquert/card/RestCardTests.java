package fr.miage.choquert.card;

import fr.miage.choquert.entities.account.Account;
import fr.miage.choquert.entities.card.Card;
import fr.miage.choquert.repositories.AccountsRepository;
import fr.miage.choquert.repositories.CardsRepository;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import java.util.UUID;

import static io.restassured.RestAssured.when;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RestCardTests {

    @LocalServerPort
    int port;

    @Autowired
    AccountsRepository accountsRepository;

    @Autowired
    CardsRepository cardsRepository;

    Account account;
    Card card;
    Card card2;

    @BeforeEach
    public void setupContect() {
        cardsRepository.deleteAll();
        accountsRepository.deleteAll();
        RestAssured.port = port;
        account = Account.builder()
                .AccountId(UUID.randomUUID().toString())
                .iban("FR9810096000505697927118M38").accountNumber("5697927118M")
                .name("Choquert").surname("Vincent").birthday("27-07-1999")
                .country("France").passport("123456789").tel("+0033636790462").secret("secret").balance(0.0)
                .build();
        accountsRepository.save(account);

        card = Card.builder()
                .cardId(UUID.randomUUID().toString())
                .cardNumber("1111111111111111").cryptogram(111).code("1234")
                .ceiling(1000.0).blocked(false).virtual(false).contact(true)
                .latitude(0.0).latitude(0.0).account(account)
                .build();
        cardsRepository.save(card);
        card2 = Card.builder()
                .cardId(UUID.randomUUID().toString())
                .cardNumber("2222222222222222").cryptogram(111).code("5678")
                .ceiling(500.0).blocked(false).virtual(false).contact(true)
                .latitude(0.0).latitude(0.0).account(account)
                .build();
        card2.setCardId(UUID.randomUUID().toString());
        card2.setCardNumber("2222222222222222");
        cardsRepository.save(card2);
    }

    @Test
    @DisplayName("Select all account cards that exist (GET /acconts/{id}/cards")
    public void getAccountCards() {
        Response response = when().get("/accounts/"+account.getAccountId()+"/cards")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response();
        String jsonAsString = response.asString();
        assertThat(jsonAsString,containsString("1111111111111111"));
        assertThat(jsonAsString,containsString("2222222222222222"));
    }

    @Test
    @DisplayName("Select all account cards but account doesn't exist (GET /accounts/{id}/cards}")
    public void getAccountCardsAccountNotFound() {
        when().get("/accounts/42/cards").then().statusCode(HttpStatus.SC_NOT_FOUND);
    }

    //GET /accounts/{id}/cards/{id}
    @Test
    @DisplayName("Select one card that exist (GET /accounts/{id}/cards/{id}")
    public void getOne() {
        Response response = when().get("/accounts/"+account.getAccountId()+"/cards/"+card.getCardId())
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response();
        String jsonAsString = response.asString();
        assertThat(jsonAsString,containsString("1111111111111111"));
    }

    @Test
    @DisplayName("Select one card but account doesn't exist (GET /accounts/{id}/cards/{id}")
    public void getOneAccountNotFound() {
        when().get("/accounts/42/cards/"+card.getCardId()).then().statusCode(HttpStatus.SC_NOT_FOUND);
    }

    @Test
    @DisplayName("Select one card that exist (GET /accounts/{id}/cards/{id}")
    public void getOneCardNotFound() {
        when().get("/accounts/"+account.getAccountId()+"/cards/42").then().statusCode(HttpStatus.SC_NOT_FOUND);
    }
}
