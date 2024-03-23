package br.com.barrigarest.tests.refact;

import br.com.barrigarest.core.BaseTest;
import io.restassured.RestAssured;
import io.restassured.specification.FilterableRequestSpecification;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

public class AuthTest extends BaseTest {

    @BeforeClass
    public static void login() {
        Map<String,String> login = new HashMap<>();
        login.put("email", "barbara@barbara");
        login.put("senha", "123456");

        String TOKEN = given()
                .body(login)
                .when()
                .post("/signin")
                .then()
                .statusCode(200)
                .extract().path("token");

        RestAssured.requestSpecification.header("Authorization", "JWT " + TOKEN);

        get("/reset").then().statusCode(200);
    }

    @Test
    public void naoDeveAcessarApiSemToken() {
        FilterableRequestSpecification requestSpecification = (FilterableRequestSpecification) RestAssured.requestSpecification;
        requestSpecification.removeHeader("Authorization");

        given()
                .when()
                .get("/contas")
                .then()
                .statusCode(401);
    }

}
