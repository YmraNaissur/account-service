package quarkus;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.h2.H2DatabaseTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.math.BigDecimal;
import java.util.List;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
@QuarkusTestResource(H2DatabaseTestResource.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AccountResourceTest {
    @Test
    @Order(1)
    void testRetrieveAll() {
        Response result = given()
                .when().get("/accounts")
                .then().statusCode(200)
                .body(
                        containsString("John Connor"),
                        containsString("Steve Jobs"),
                        containsString("Brian Holland")
                )
                .extract()
                .response();

        List<Account> accounts = result.jsonPath().getList("$");
        assertThat(accounts, not(empty()));
        assertThat(accounts, hasSize(5));
    }

    @Test
    @Order(2)
    void testCreateAccount() {
        Account newAccount = new Account();
        newAccount.setAccountNumber(10L);
        newAccount.setCustomerNumber(10L);
        newAccount.setCustomerName("Jason Biggs");

        Account returnedAccount = given()
                .contentType(ContentType.JSON)
                .body(newAccount)
                .when().post("/accounts")
                .then()
                .statusCode(200)
                .extract()
                .as(Account.class);

        assertThat(returnedAccount, notNullValue());
        assertThat(returnedAccount, equalTo(newAccount));

        Response result = given()
                .when().get("/accounts")
                .then()
                .statusCode(200)
                .body(
                        containsString("John Connor"),
                        containsString("Steve Jobs"),
                        containsString("Brian Holland"),
                        containsString("Jason Biggs")
                )
                .extract()
                .response();

        List<Account> accounts = result.jsonPath().getList("$");
        assertThat(accounts, not(empty()));
        assertThat(accounts, hasSize(6));
    }

    @Test
    void testGetAccount() {
        Account account = given()
                .when().get("/accounts/{accountNumber}", 1)
                .then()
                .statusCode(200)
                .extract()
                .as(Account.class);

        assertThat(account.getAccountNumber(), equalTo(1L));
        assertThat(account.getCustomerName(), equalTo("John Connor"));
        assertThat(account.getBalance(), equalTo(new BigDecimal("550.78")));
        assertThat(account.getAccountStatus(), equalTo(AccountStatus.OPEN));
    }

    @Test
    void closeAccountTest() {
        Response result = given()
                .when().get("/accounts/close/{accountNumber}", 1)
                        .then()
                        .statusCode(200)
                        .extract()
                        .response();

        String status = result.jsonPath().getString("accountStatus");
        Double balance = result.jsonPath().getDouble("balance");
        assertEquals("CLOSED", status);
        assertEquals(0.0, balance);
    }

    @Test
    void withdrawTest() {
        Response result = given()
                .when().queryParam("accountNumber", 2)
                .queryParam("value", 389.32)
                .get("/accounts/withdraw")
                .then()
                .statusCode(200)
                .extract()
                .response();

        Double balance = result.jsonPath().getDouble("balance");
        assertEquals(2000.0, balance);
    }

    @Test
    void depositTest() {
        Response result = given()
                .when().queryParam("accountNumber", 3)
                .queryParam("value", 500.88)
                .get("/accounts/deposit")
                .then()
                .statusCode(200)
                .extract()
                .response();

        Double balance = result.jsonPath().getDouble("balance");
        assertEquals(4000.0, balance);
    }
}