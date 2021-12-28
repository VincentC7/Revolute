package fr.miage.choquert.account;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.Matchers.not;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.miage.choquert.Util;
import fr.miage.choquert.entities.account.Account;
import fr.miage.choquert.entities.account.AccountInput;
import fr.miage.choquert.repositories.AccountsRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.apache.http.HttpStatus;

import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RestAccountTests {

	@LocalServerPort
	int port;

	@Autowired
	AccountsRepository accountsRepository;

	Account account;

	@BeforeEach
	public void setupContect(){
		accountsRepository.deleteAll();
		RestAssured.port = port;
		account = Account.builder()
				.AccountId(UUID.randomUUID().toString())
				.iban("FR9810096000505697927118M38").accountNumber("5697927118M")
				.name("Choquert").surname("Vincent").birthday("27-07-1999")
				.country("France").passport("123456789").tel("+0033636790462").secret("secret").balance(0.0)
				.build();
		accountsRepository.save(account);
	}

	@Test
	@DisplayName("Select one account that exist")
	public void getOne(){
		Response response = when().get("/accounts/"+account.getAccountId())
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
	@DisplayName("Create account that should works")
	public void postAccount() throws Exception {
		AccountInput accountInput = AccountInput.builder()
				.name("Choquert").surname("Vincent").birthday("27-07-1999")
				.country("France").passport("123456789").tel("+0033636790462").secret("secret")
				.build();
		Response response = given()
				.body(Util.toJsonString(accountInput))
				.contentType(ContentType.JSON)
				.when()
				.post("/accounts")
				.then()
				.statusCode(HttpStatus.SC_CREATED)
				.extract().response();
		String location = response.getHeader("location");
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
		AccountInput accountInput = AccountInput.builder()
				.name(name).surname(surname).birthday(birthday)
				.country(country).passport(passport).tel(tel).secret(secret)
				.build();
		given()
				.body(Util.toJsonString(accountInput))
				.contentType(ContentType.JSON)
				.when()
				.post("/accounts")
				.then()
				.statusCode(HttpStatus.SC_BAD_REQUEST);
	}


	@Test
	@DisplayName("patch account succes")
	public void patchAccount() throws Exception {
		AccountInput accountInput = AccountInput.builder()
				.name("super").surname("test").birthday(account.getBirthday())
				.tel(account.getTel()).country(account.getCountry()).passport(account.getPassport())
				.secret(account.getSecret())
				.build();
		Response response = given()
				.body(Util.toJsonString(accountInput))
				.contentType(ContentType.JSON)
				.when()
				.patch("/accounts/"+account.getAccountId())
				.then()
				.statusCode(HttpStatus.SC_OK)
				.extract().response();
		String jsonAsString = response.asString();
		assertThat(jsonAsString,containsString("\"name\" : \"super\","));
		assertThat(jsonAsString,not(containsString("\"name\" : \"Choquert\",")));
	}

	@Test
	@DisplayName("patch one account that exist but params is wrong")
	public void patchAccountFail() throws Exception {
		AccountInput accountInput = AccountInput.builder().tel("test").build();
		given()
				.body(Util.toJsonString(accountInput))
				.contentType(ContentType.JSON)
				.when()
				.patch("/accounts/"+account.getAccountId())
				.then()
				.statusCode(HttpStatus.SC_BAD_REQUEST);
	}

	@Test
	@DisplayName("patch one account that not exist")
	public void patchNotFound() throws Exception {
		AccountInput accountInput = AccountInput.builder().name("Choquert").build();
		given().body(Util.toJsonString(accountInput))
				.contentType(ContentType.JSON)
				.patch("/accounts/42")
				.then()
				.statusCode(HttpStatus.SC_NOT_FOUND);
	}
}
