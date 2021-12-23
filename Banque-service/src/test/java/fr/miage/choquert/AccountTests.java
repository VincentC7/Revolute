package fr.miage.choquert;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.Matchers.not;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.miage.choquert.entities.account.Account;
import fr.miage.choquert.entities.account.AccountInput;
import fr.miage.choquert.repositories.AccountsRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.apache.http.HttpStatus;

import java.time.Instant;
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
	@DisplayName("Select one account that exist")
	public void getOne(){
		Account account = Account.builder()
				.id(UUID.randomUUID().toString())
				.iban("FR9810096000505697927118M38").accountNumber("5697927118M")
				.name("Choquert").surname("Vincent").birthday("27-07-1999")
				.country("France").passport("123456789").tel("0033 636 790 462").secret("secret").balance(0.0)
				.build();
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
	@DisplayName("Select one account that not exist")
	public void getNotFound(){
		when().get("/accounts/42").then().statusCode(HttpStatus.SC_NOT_FOUND);
	}

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


	@Test
	@DisplayName("Create account that should works")
	public void postAccount() throws Exception {
		AccountInput accountInput = new AccountInput("Choquert", "Vincent","27-07-1999", "France", "123456789", "+0033636790462", "secret");
		Response response = given()
				.body(this.toJsonString(accountInput))
				.contentType(ContentType.JSON)
				.when()
				.post("/accounts")
				.then()
				.statusCode(HttpStatus.SC_CREATED)
				.extract().response();
		String location = response.getContentType();
		when().get(location).then().statusCode(HttpStatus.SC_OK);
	}


	@ParameterizedTest
	@CsvSource(value = {
			//test blank
			", Vincent, 27-07-1999, France, 123456789, +0033636790462, secret",
			"Choquert,, 27-07-1999, France, 123456789, +0033636790462, secret",
			"Choquert, Vincent,, France, 123456789, +0033636790462, secret",
			"Choquert, Vincent, 27-07-1999,, 123456789, +0033636790462, secret",
			"Choquert, Vincent, 27-07-1999, France,, +0033636790462, secret",
			"Choquert, Vincent, 27-07-1999, France, 123456789,, secret",
			"Choquert, Vincent, 27-07-1999, France, 123456789, +0033636790462,",

			//test null
			"null, Vincent, 27-07-1999, France, 123456789, +0033636790462, secret",
			"Choquert, null, 27-07-1999, France, 123456789, +0033636790462, secret",
			"Choquert, Vincent, null, France, 123456789, +0033636790462, secret",
			"Choquert, Vincent, 27-07-1999, null, 123456789, +0033636790462, secret",
			"Choquert, Vincent, 27-07-1999, France, null, +0033636790462, secret",
			"Choquert, Vincent, 27-07-1999, France, 123456789, null, secret",
			"Choquert, Vincent, 27-07-1999, France, 123456789, +0033636790462, null",

			//test tel
			"Choquert, Vincent, 27-07-1999, France, 123456789, untelquimarchepas, secret",

			//test birthday
			"Choquert, Vincent, 1999-07-27, France, 123456789, +0033636790462, secret",
			"Choquert, Vincent, 32-07-1999, France, 123456789, +0033636790462, secret",
			"Choquert, Vincent, nimportequoi, France, 123456789, +0033636790462, secret",
			"Choquert, Vincent, 27/07/1999, France, 123456789, +0033636790462, secret",
			"Choquert, Vincent, 27-07-0000, France, 123456789, +0033636790462, secret",

	}, nullValues={"null"})
	public void postAccountFail(String name, String surname, String birthday, String country, String passport, String tel, String secret) throws Exception {
		AccountInput accountInput = new AccountInput(name, surname, birthday, country, passport, tel, secret);
		given()
				.body(this.toJsonString(accountInput))
				.contentType(ContentType.JSON)
				.when()
				.post("/accounts")
				.then()
				.statusCode(HttpStatus.SC_BAD_REQUEST);
	}

	private String toJsonString(Object o) throws Exception {
		ObjectMapper map = new ObjectMapper();
		return map.writeValueAsString(o);
	}



}
