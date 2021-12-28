package fr.miage.choquert.card;

import fr.miage.choquert.Util;
import fr.miage.choquert.entities.account.Account;
import fr.miage.choquert.entities.account.AccountInput;
import fr.miage.choquert.entities.card.Card;
import fr.miage.choquert.entities.card.CardInput;
import fr.miage.choquert.repositories.AccountsRepository;
import fr.miage.choquert.repositories.CardsRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;

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


    //POST /accounts/{id}/cards
    @Test
    @DisplayName("Create card that should work")
    public void postCard() throws Exception {
        CardInput cardInput = CardInput.builder()
                .code("1234").ceiling(1000).virtual(false)
                .blocked(false).contact(true).longitude(48.52).latitude(2.19)
                .build();
        Response response = given()
                .body(Util.toJsonString(cardInput))
                .contentType(ContentType.JSON)
                .when()
                .post("/accounts/"+account.getAccountId()+"/cards")
                .then()
                .statusCode(HttpStatus.SC_CREATED)
                .extract().response();
        String location = response.getHeader("location");
        when().get(location).then().statusCode(HttpStatus.SC_OK);
    }

    @ParameterizedTest
    @CsvSource(value = {
            //code
            ", 1000, false, false, true, 48.52, 2.19",
            "null, 1000, false, false, true, 48.52, 2.19",
            "mauvaiscode, 1000, false, false, true, 48.52, 2.19",

    }, nullValues={"null"})
    public void postCardFail(String code, double ceiling, boolean virtual, boolean blocked, boolean contact, double longitude, double latitude) throws Exception {
        CardInput cardInput = CardInput.builder()
                .code(code).ceiling(ceiling).virtual(virtual)
                .blocked(blocked).contact(contact).longitude(longitude).latitude(latitude)
                .build();
        given()
                .body(Util.toJsonString(cardInput))
                .contentType(ContentType.JSON)
                .when()
                .post("/accounts/"+account.getAccountId()+"/cards")
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    @DisplayName("Create card but account not found")
    public void postCardAccountNotFound() throws Exception {
        CardInput cardInput = CardInput.builder()
                .code("1234").ceiling(1000).virtual(false)
                .blocked(false).contact(true).longitude(48.52).latitude(2.19)
                .build();
        given()
                .body(Util.toJsonString(cardInput))
                .contentType(ContentType.JSON)
                .when()
                .post("/accounts/42/cards")
                .then()
                .statusCode(HttpStatus.SC_NOT_FOUND);
    }

    //PATCH /accounts/{id}/cards/{id}
    @Test
    @DisplayName("patch account succes")
    public void patchCard() throws Exception {
        Response response = given()
                .body("{\"code\":\"9999\"}")
                .contentType(ContentType.JSON)
                .when()
                .patch("/accounts/"+account.getAccountId()+"/cards/"+card.getCardId())
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract().response();
        String jsonAsString = response.asString();
        assertThat(jsonAsString,containsString("\"code\" : \"9999\","));
        assertThat(jsonAsString,not(containsString("\"code\" : \"1234\",")));
    }

    @Test
    @DisplayName("patch one card that exist but params is wrong")
    public void patchCardFail() {
        String json = "{\"code\":\"bad\"}";
        given()
                .body(json)
                .contentType(ContentType.JSON)
                .when()
                .patch("/accounts/"+account.getAccountId()+"/cards/"+card.getCardId())
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST);
    }


    @ParameterizedTest
    @ValueSource(strings = {"cardId", "cardNumber", "cryptogram","contact","virtual", "account"})
    @DisplayName("patch one card that exist but params can't be changed")
    public void patchCardFailImmutableParams(String immutableParam) {
        String json = "{\""+immutableParam+"\":\"123\"}";
        given()
                .body(json)
                .contentType(ContentType.JSON)
                .when()
                .patch("/accounts/"+account.getAccountId()+"/cards/"+card.getCardId())
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    @DisplayName("patch one account that not exist")
    public void patchCardAccountNotFound() {
        given().body("{\"code\":\"9999\"}")
                .contentType(ContentType.JSON)
                .patch("/accounts/42/cards/"+card.getCardId())
                .then()
                .statusCode(HttpStatus.SC_NOT_FOUND);
    }

    @Test
    @DisplayName("patch one account that not exist")
    public void patchCardNotFound() {
        given().body("{\"code\":\"9999\"}")
                .contentType(ContentType.JSON)
                .patch("/accounts/"+account.getAccountId()+"/cards/42")
                .then()
                .statusCode(HttpStatus.SC_NOT_FOUND);
    }

}
