package fr.miage.choquert;


import fr.miage.choquert.entities.ExchangeRate;
import fr.miage.choquert.repositories.ExchangeRateRepository;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import java.math.BigDecimal;

import static io.restassured.RestAssured.when;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ExchangeRateTests {

    @LocalServerPort
    int port;

    @Autowired
    ExchangeRateRepository exchangeRateRepository;

    ExchangeRate exchangeRate;

    @BeforeEach
    public void setupContect(){
        exchangeRateRepository.deleteAll();
        RestAssured.port = port;
        exchangeRate = ExchangeRate.builder()
                .id(1L)
                .source("EUR")
                .target("USD")
                .rate(new BigDecimal("1.1365"))
                .build();
        exchangeRateRepository.save(exchangeRate);
    }

    @Test
    @DisplayName("Get one good")
    public void getOne(){
        Response response = when().get("/conversion-devise/source/EUR/target/USD/amount/1000")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response();
        String jsonAsString = response.asString();
        assertThat(jsonAsString,containsString("afterConversion\":1140.00"));
        assertThat(jsonAsString,containsString("\"rate\":1.14}"));
    }

    @ParameterizedTest
    @CsvSource(value = {
            "EUR, BTC",
            "BTC, EUR",
            "111, EUR",
            "BTC, 111",
    }, nullValues={"null"})
    public void getNotFound(String source, String target){
        Response response = when().get("/conversion-devise/source/"+source+"/target/"+target+"/amount/1000")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response();
        String jsonAsString = response.asString();
        assertThat(jsonAsString,containsString("\"rate\":-1"));
    }

    @Test
    @DisplayName("Get one whith bad params")
    public void getOneBadRequest(){
        when().get("/conversion-devise/source/EUR/target/USD/amount/BADPARAM")
                .then().statusCode(HttpStatus.SC_BAD_REQUEST);
    }
}
