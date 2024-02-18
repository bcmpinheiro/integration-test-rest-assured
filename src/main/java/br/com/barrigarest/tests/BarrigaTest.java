package br.com.barrigarest.tests;

import io.restassured.http.ContentType;
import org.junit.Test;
import java.util.HashMap;
import java.util.Map;
import static io.restassured.RestAssured.given;

public class BarrigaTest {

    @Test
    public void naoDeveAcessarApiSemToken() {
        Map<String,String> login = new HashMap<String, String>();
        login.put("email", "barbara@barbara");
        login.put("senha", "123456");

        String token = given()
                .log().all()
                .body(login)
                .contentType(ContentType.JSON)
                .when()
                .post("https://barrigarest.wcaquino.me/signin")
                .then()
                .log().all()
                .statusCode(200)
                .extract().path("token");

        given()
                .log().all()
                .header("Authorization", "JWT ")
                .when()
                .get("https://barrigarest.wcaquino.me/contas")
                .then()
                .log().all()
                .statusCode(401);
    }

}
