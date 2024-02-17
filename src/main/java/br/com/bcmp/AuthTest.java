package br.com.bcmp;

import io.restassured.http.ContentType;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

public class AuthTest {

    @Test
    public void deveAcessarAPISW() {
        given()
                    .log().all()
                .when()
                    .get("https://swapi.dev/api/people/1")
                .then()
                    .log().all()
                    .contentType(ContentType.JSON)
                    .statusCode(200)
                    .body("name", is("Luke Skywalker"))
                ;

    }


}
