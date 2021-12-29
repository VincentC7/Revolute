package fr.miage.choquert.operation;

import fr.miage.choquert.entities.account.Account;
import fr.miage.choquert.entities.operation.Operation;
import fr.miage.choquert.repositories.AccountsRepository;
import fr.miage.choquert.repositories.OperationRepository;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import java.time.Instant;
import java.util.UUID;

import static io.restassured.RestAssured.when;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RestOperationTests {

    @LocalServerPort
    int port;

    @Autowired
    AccountsRepository accountsRepository;

    @Autowired
    OperationRepository operationRepository;

    Account account;
    Operation o1,o2;

    @BeforeEach
    public void setupContect() {
        operationRepository.deleteAll();
        accountsRepository.deleteAll();
        RestAssured.port = port;
        account = Account.builder()
                .accountId(UUID.randomUUID().toString())
                .iban("FR9810096000505697927118M38").accountNumber("5697927118M")
                .name("Choquert").surname("Vincent").birthday("27-07-1999")
                .country("France").passport("123456789").tel("+0033636790462").secret("secret").balance(0.0)
                .build();
        accountsRepository.save(account);

        o1 = Operation.builder()
                .operationId(UUID.randomUUID().toString())
                .country("France").datePerformed(Instant.now()).montant(5).ibanCrediteur("FR1111111111111111111111M38")
                .libelle("test d'achat").categorie("Divers").account(account).virement(false)
                .build();
        operationRepository.save(o1);
        o2 = Operation.builder()
                .operationId(UUID.randomUUID().toString())
                .country("France").datePerformed(Instant.now()).montant(1500).ibanCrediteur("FR2222222222222222222222M38")
                .libelle("un achat trop bien").categorie("Divers").account(account).virement(false)
                .account(account)
                .build();
        operationRepository.save(o2);
    }

    @Test
    @DisplayName("Select all account operations that exist (GET /acconts/{id}/operations")
    public void getAccountOperations() {
        Response response = when().get("/accounts/"+account.getAccountId()+"/operations")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response();
        String jsonAsString = response.asString();
        assertThat(jsonAsString,containsString("FR1111111111111111111111M38"));
        assertThat(jsonAsString,containsString("FR2222222222222222222222M38"));
    }

    @Test
    @DisplayName("Select all account operations but account doesn't exist (GET /accounts/{id}/operations}")
    public void getAccountCardsAccountNotFound() {
        when().get("/accounts/42/operations").then().statusCode(HttpStatus.SC_NOT_FOUND);
    }

    //GET /accounts/{id}/operations/{id}
    @Test
    @DisplayName("Select one operation that exist (GET /accounts/{id}/operations/{id}")
    public void getOne() {
        Response response = when().get("/accounts/"+account.getAccountId()+"/operations/"+o1.getOperationId())
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response();
        String jsonAsString = response.asString();
        assertThat(jsonAsString,containsString("FR1111111111111111111111M38"));
    }

    @Test
    @DisplayName("Select one operation but account doesn't exist (GET /accounts/{id}/operations/{id}")
    public void getOneAccountNotFound() {
        when().get("/accounts/42/operations/"+o1.getOperationId()).then().statusCode(HttpStatus.SC_NOT_FOUND);
    }

    @Test
    @DisplayName("Select one operation that exist (GET /accounts/{id}/operations/{id}")
    public void getOneCardNotFound() {
        when().get("/accounts/"+account.getAccountId()+"/operations/42").then().statusCode(HttpStatus.SC_NOT_FOUND);
    }

}
