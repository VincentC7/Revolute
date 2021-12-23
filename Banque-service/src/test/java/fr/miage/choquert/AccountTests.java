package fr.miage.choquert;

import static io.restassured.RestAssured.when;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;

import fr.miage.choquert.entities.account.Account;
import fr.miage.choquert.repositories.AccountsRepository;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.apache.http.HttpStatus;

import java.time.LocalDate;
import java.time.Month;
import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AccountTests {

	@LocalServerPort
	int port;

	@Autowired
	AccountsRepository accountsRepository;

	@BeforeEach
	public void setupContect(){
		accountsRepository.deleteAll();
		RestAssured.port = port;
	}

	@Test
	public void getOne(){
		Account account = new Account(UUID.randomUUID().toString(),
				"FRkk BBBB BGGG GG01 2345 67E8 9KK","01234567E89",
				"Choquert", "Vincent", LocalDate.of(1999, Month.JULY,27),
				"France","123456789", "0033 636 790 462", "secret", 0.0);
		accountsRepository.save(account);
		Response response = when().get("/accounts/"+account.getId())
				.then()
				.statusCode(HttpStatus.SC_OK)
				.extract()
				.response();
		String jsonAsString = response.asString();
		assertThat(jsonAsString,containsString("Vincent"));
	}

	@Test
	public void getNotFound(){
		when().get("/accounts/42").then().statusCode(HttpStatus.SC_NOT_FOUND);
	}

}
