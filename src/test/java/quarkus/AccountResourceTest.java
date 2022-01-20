package quarkus;

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
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AccountResourceTest {
/*    @Test
    @Order(1)
    void testRetrieveAll() {
        Response result = given()
                .when().get("/accounts")
                .then().statusCode(200)
                .body(
                        containsString("Max Karavaev"),
                        containsString("Alexander Korneev"),
                        containsString("Artem Zhvikov")
                )
                .extract()
                .response();

        List<Account> accounts = result.jsonPath().getList("$");
        assertThat(accounts, not(empty()));
        assertThat(accounts, hasSize(3));
    }

    @Test
    @Order(2)
    void testGetAccount() {
        Account account = given()
                .when().get("/accounts/{accountNumber}", 1)
                .then()
                .statusCode(200)
                .extract()
                .as(Account.class);

        assertThat(account.getAccountNumber(), equalTo(1L));
        assertThat(account.getCustomerName(), equalTo("Max Karavaev"));
        assertThat(account.getBalance(), equalTo(new BigDecimal("678000")));
        assertThat(account.getAccountStatus(), equalTo(AccountStatus.OPEN));
    }

    @Test
    @Order(3)
    void testCreateAccount() {
        Account newAccount = new Account(5L, 12L, "Ruslan Volovik", new BigDecimal("48500"));

        Account returnedAccount = given()
                .contentType(ContentType.JSON)
                .body(newAccount)
                .when().post("/accounts")
                .then()
                .statusCode(201)
                .extract()
                .as(Account.class);

        assertThat(returnedAccount, notNullValue());
        assertThat(returnedAccount, equalTo(newAccount));

        Response result = given()
                .when().get("/accounts")
                .then()
                .statusCode(200)
                .body(
                        containsString("Max Karavaev"),
                        containsString("Alexander Korneev"),
                        containsString("Artem Zhvikov"),
                        containsString("Ruslan Volovik")
                )
                .extract()
                .response();

        List<Account> accounts = result.jsonPath().getList("$");
        assertThat(accounts, not(empty()));
        assertThat(accounts, hasSize(4));
    }*/
}