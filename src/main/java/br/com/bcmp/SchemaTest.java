package br.com.bcmp;

import io.restassured.matcher.RestAssuredMatchers;
import io.restassured.module.jsv.JsonSchemaValidator;
import org.junit.Test;

import static io.restassured.RestAssured.given;

public class SchemaTest {

    @Test
    public void deveValidarSchemaJSON() {
        given()
                    .log().all()
                .when()
                    .get("https://restapi.wcaquino.me/users")
                .then()
                    .log().all()
                    .statusCode(200)
                    .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("users.json"));
    }
}
