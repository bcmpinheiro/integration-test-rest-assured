package br.com.bcmp;

import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;

public class EnvioDadosTeste {

    @Test
    public void deveEnviarDadoViaQuery() {
        given()
                    .log().all()
                .when()
                    .get("https://restapi.wcaquino.me/v2/users?format=json")
                .then()
                    .log().all()
                    .statusCode(200)
                    .contentType(ContentType.JSON);
    }

    @Test
    public void deveEnviarDadoViaQueryViaParam() {
        given()
                    .log().all()
                    .queryParam("format", "json")
                .when()
                    .get("https://restapi.wcaquino.me/v2/users")
                .then()
                    .log().all()
                    .statusCode(200)
                    .contentType(containsString("utf-8"))
                    .contentType(ContentType.JSON);
    }

    @Test
    public void deveEnviarDadoViaHeader() {
        given()
                    .log().all()
                    .accept(ContentType.JSON)
                .when()
                    .get("https://restapi.wcaquino.me/v2/users")
                .then()
                    .log().all()
                    .statusCode(200)
                    .contentType(ContentType.HTML);
    }

}
