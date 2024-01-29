package br.com.bcmp;

import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class OlaMundoTest {

    @Test
    public void testOlaMundo() {
        Response response = RestAssured.request(Method.GET, "https://restapi.wcaquino.me/ola");
        assertTrue(response.getBody().asString().equals("Ola Mundo!"));
        assertTrue(response.statusCode() == 200);
        assertEquals(response.statusCode(),200);

        ValidatableResponse validacao = response.then();
        validacao.statusCode(200);
    }

    @Test
    public void devoConhecerOutrasFormasRestAssured() {
        Response response = request(Method.GET, "https://restapi.wcaquino.me/ola");
        ValidatableResponse validacao = response.then();
        validacao.statusCode(200);

        get("https://restapi.wcaquino.me/ola").then().statusCode(200);

        //Given, When, Then - Dado, Quando, Entao

        given()
                //pre condicoes (ex:header)
                .when()
                    //acao a ser testada
                    .get("https://restapi.wcaquino.me/ola")
                .then()
                    //assertivas
                    //.assertThat()
                    .statusCode(200);
    }

    @Test
    public void devoConhecerMatchersHamcrest() {
        //igualdades
        assertThat("Maria", is("Maria"));
        assertThat(128, is(128));
        assertThat(128, isA(Integer.class));
        assertThat(128d, isA(Double.class));
        assertThat(128, greaterThan(120));
        assertThat(128, lessThan(130));

        //listas
        List<Integer> impares = Arrays.asList(1,3,5,7,9);
        assertThat(impares, hasSize(5));
        assertThat(impares, contains(1,3,5,7,9));
        assertThat(impares, containsInAnyOrder(1,3,5,9,7));
        assertThat(impares, hasItem(1));
        assertThat(impares, hasItems(1,5));

        //assertivas na mesma logica
        assertThat("Maria", is(not("Joao")));
        assertThat("Maria", not("Joao"));
        assertThat("Joaquina", anyOf(is("Maria"), is("Joaquina")));
        assertThat("Joaquina", allOf(startsWith("Joa"), endsWith("ina"), containsString("qui")));
    }

    @Test
    public void devoValidarBody() {
        given()
                .when()
                    .get("https://restapi.wcaquino.me/ola")
                .then()
                    .statusCode(200)
                    .body(Matchers.is("Ola Mundo!"))
                    .body(containsString("Mundo"))
                    .body(is(notNullValue()));
    }
}
