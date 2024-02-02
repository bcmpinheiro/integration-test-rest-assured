import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.theories.suppliers.TestedOn;

import java.util.ArrayList;
import java.util.Arrays;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class UserJsonTest {

    @BeforeClass
    public static void setup() {
        baseURI = "https://restapi.wcaquino.me";
        //port = 443;
        //basePath = "/v1";
    }

    @Test
    public void deveVerificarPrimeiroNivel() {

        given()
                .log().all()
                .when()
                    .get("/users/1")
                .then()
                    .statusCode(200)
                    .body("id", is(1))
                    .body("name", containsString("Silva"))
                    .body("age", greaterThan(18));
    }

    @Test
    public void deveVerificarPrimeiroNivelOutrasFormas() {

        Response response = request(Method.GET, "https://restapi.wcaquino.me/users/1");

        //path
        Assert.assertEquals(new Integer(1), response.path("id"));
        Assert.assertEquals(new Integer(1), response.path("%s", "id"));

        //jsonpath
        JsonPath jsonPath = new JsonPath(response.asString());
        Assert.assertEquals(1, jsonPath.getInt("id"));

        //from
        int id = JsonPath.from(response.asString()).getInt("id");
        Assert.assertEquals(1, id);
    }

    @Test
    public void deveVerificarSegundoNivel() {

        given()
                .log().all()
                .when()
                    .get("/users/2")
                .then()
                    .statusCode(200)
                    .body("name", containsString("Joaquina"))
                    .body("endereco.rua", is("Rua dos bobos"));
    }

    @Test
    public void deveVerificarLista() {

        given()
                .log().all()
                .when()
                    .get("/users/3")
                .then()
                    .statusCode(200)
                    .body("name", containsString("Ana"))
                    .body("filhos", hasSize(2))
                    .body("filhos[0].name", is("Zezinho"))
                    .body("filhos[1].name", is("Luizinho"))
                    .body("filhos[1].name", is("Luizinho"))
                    .body("filhos.name", hasItem("Zezinho"))
                    .body("filhos.name", hasItems("Zezinho", "Luizinho"));
    }

    @Test
    public void deveRetornarErroUsuarioInexistente() {

        given()
                .log().all()
                .when()
                    .get("/users/4")
                .then()
                    .statusCode(404)
                    .body("error", is("Usuário inexistente"));
    }

    @Test
    public void deveVerificarListaNaRaiz() {

        given()
                .log().all()
                .when()
                    .get("/users")
                .then()
                    .statusCode(200)
                    .body("$", hasSize(3))
                    .body("name", hasItems("João da Silva", "Maria Joaquina", "Ana Júlia"))
                    .body("age[1]", is(25))
                    .body("filhos.name", hasItem(Arrays.asList("Zezinho", "Luizinho")))
                    .body("salary", contains(1234.5678f, 2500, null));
    }

    @Test
    public void deveFazerVerificacoesAvancadas() {

        given()
                .log().all()
                .when()
                    .get("/users")
                .then()
                    .statusCode(200)
                    .body("$", hasSize(3))
                    .body("age.findAll{it <= 25}.size()",is(2))
                    .body("age.findAll{it >20 && it <= 25}.size()",is(1))
                    .body("findAll{it.age >20 && it.age <= 25}.name",hasItem("Maria Joaquina"))
                    .body("findAll{it.age <= 25}[0].name",is("Maria Joaquina"))
                    .body("findAll{it.age <= 25}[-1].name",is("Ana Júlia"))
                    .body("find{it.age <= 25}.name",is("Maria Joaquina"))
                    .body("findAll{it.name.contains('n')}.name",hasItems("Maria Joaquina","Ana Júlia"))
                    .body("findAll{it.name.length() > 10}.name",hasItems("João da Silva", "Maria Joaquina"))
                    .body("name.collect{it.toUpperCase()}", hasItem("MARIA JOAQUINA"))
                    .body("name.findAll{it.startsWith('Maria')}.collect{it.toUpperCase()}", hasItem("MARIA JOAQUINA"))
                    .body("name.findAll{it.startsWith('Maria')}.collect{it.toUpperCase()}.toArray()", allOf(arrayContaining("MARIA JOAQUINA"),arrayWithSize(1)))
                    .body("age.collect{it * 2}", hasItems(60, 50, 40))
                    .body("id.max()", is(3))
                    .body("salary.min()", is(1234.5678f))
                    .body("salary.findAll{it != null}.sum()", is(closeTo(3734.5678f, 0.001)))
                    .body("salary.findAll{it != null}.sum()", allOf(greaterThan(3000d), lessThan(5000d)));
    }

    @Test
    public void deveUnirJsonPathComJava() {

        ArrayList<String> names =
        given()
                .log().all()
                .when()
                    .get("/users")
                .then()
                    .statusCode(200)
                    .extract().path("name.findAll{it.startsWith('Maria Joaquina')}")

                ;
        Assert.assertEquals(1, names.size());
        Assert.assertTrue(names.get(0).equalsIgnoreCase("mArIa JoAqUiNa"));
        Assert.assertEquals(names.get(0).toUpperCase(), "maria joaquina".toUpperCase());
    }
}
