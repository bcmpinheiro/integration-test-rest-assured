package br.com.bcmp;

import org.junit.Test;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class VerbosHttpTest {

    @Test
    public void deveSalvarUsuario() {
        given()
                .log().all()
                .contentType("application/json")
                .body("{ \"name\": \"Jose\", \"age\": 50}")
                .when()
                    .post("https://restapi.wcaquino.me/users")
                .then()
                    .log().all()
                    .statusCode(201)
                    .body("id", is(notNullValue()))
                    .body("name", is("Jose"))
                    .body("age", is(50));
    }

    @Test
    public void naoDeveSalvarUsuarioSemNome() {
        given()
                    .log().all()
                    .contentType("application/json")
                    .body("{ \"age\": 50}")
                .when()
                    .post("https://restapi.wcaquino.me/users")
                .then()
                    .log().all()
                    .statusCode(400)
                    .body("id", is(nullValue()))
                    .body("error", is("Name é um atributo obrigatório"));
    }

    @Test
    public void deveAlterarUsuario() {
        given()
                    .log().all()
                    .contentType("application/json")
                    .body("{ \"name\": \"Usuario Alterado\", \"age\": 80}")
                .when()
                    .put("https://restapi.wcaquino.me/users/1")
                .then()
                    .log().all()
                    .statusCode(200)
                    .body("id", is(1))
                    .body("name", is("Usuario Alterado"))
                    .body("age", is(80))
                    .body("salary", is(1234.5678f));
    }

    @Test
    public void devoCustomizarURL() {
        given()
                    .log().all()
                    .contentType("application/json")
                    .body("{ \"name\": \"Usuario Alterado\", \"age\": 80}")
                .when()
                    .put("https://restapi.wcaquino.me/{entidade}/{userId}", "users", "1")
                .then()
                    .log().all()
                    .statusCode(200)
                    .body("id", is(1))
                    .body("name", is("Usuario Alterado"))
                    .body("age", is(80))
                    .body("salary", is(1234.5678f));
    }

    @Test
    public void devoCustomizarURLParte2() {
        given()
                    .log().all()
                    .contentType("application/json")
                    .body("{ \"name\": \"Usuario Alterado\", \"age\": 80}")
                    .pathParam("entidade", "users")
                    .pathParam("userId", "1")
                .when()
                    .put("https://restapi.wcaquino.me/{entidade}/{userId}")
                .then()
                    .log().all()
                    .statusCode(200)
                    .body("id", is(1))
                    .body("name", is("Usuario Alterado"))
                    .body("age", is(80))
                    .body("salary", is(1234.5678f));
    }

    @Test
    public void deveRemoverUsuario() {
        given()
                    .log().all()
                    .pathParam("entidade", "users")
                    .pathParam("userId", "1")
                .when()
                    .delete("https://restapi.wcaquino.me/{entidade}/{userId}")
                .then()
                    .log().all()
                    .statusCode(204);
    }

    @Test
    public void naoDeveRemoverUsuarioInexistente() {
        given()
                .log().all()
                .pathParam("entidade", "users")
                .pathParam("userId", "1000")
                .when()
                .delete("https://restapi.wcaquino.me/{entidade}/{userId}")
                .then()
                .log().all()
                .statusCode(400)
                .body("error", is("Registro inexistente"));
    }
}