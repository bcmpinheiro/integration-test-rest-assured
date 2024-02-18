package br.com.bcmp;

import io.restassured.http.ContentType;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.hasItem;
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
                    .body("name", is("Luke Skywalker"));
    }

    @Test
    public void deveValidarApi() {
        given()
                    .log().all()
                .when()
                    .get("https://api.adviceslip.com/advice/5")
                .then()
                    .log().all()
                    .contentType(ContentType.HTML)
                    .statusCode(200)
                    .body("html.body.size()", is(1));
    }

    @Test
    public void naoDeveAcessarSemSenha() {
        given()
                    .log().all()
                .when()
                    .get("https://restapi.wcaquino.me/basicauth")
                .then()
                    .log().all()
                    .statusCode(401);
    }

    @Test
    public void deveFazerAutenticacaoBasica() {
        given()
                    .log().all()
                .when()
                    .get("https://admin:senha@restapi.wcaquino.me/basicauth")
                .then()
                    .log().all()
                    .statusCode(200)
                    .body("status", is ("logado"));
    }

    @Test
    public void deveFazerAutenticacaoBasica2() {
        given()
                    .log().all()
                .auth().basic("admin", "senha")
                .when()
                    .get("https://restapi.wcaquino.me/basicauth")
                .then()
                    .log().all()
                    .statusCode(200)
                    .body("status", is ("logado"));
    }

    @Test
    public void deveFazerAutenticacaoBasicaChallenge() {
        given()
                    .log().all()
                    .auth().preemptive().basic("admin", "senha")
                .when()
                    .get("https://restapi.wcaquino.me/basicauth2")
                .then()
                    .log().all()
                    .statusCode(200)
                    .body("status", is ("logado"));
    }

    @Test
    public void deveFazerAutenticacaoComToken() {
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
                .header("Authorization", "JWT " + token)
                .when()
                    .get("https://barrigarest.wcaquino.me/contas")
                .then()
                    .log().all()
                    .statusCode(200)
                .body("nome", hasItem("teste"));
    }

}
