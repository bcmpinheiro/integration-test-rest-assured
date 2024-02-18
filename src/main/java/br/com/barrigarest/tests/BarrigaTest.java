package br.com.barrigarest.tests;

import br.com.barrigarest.core.BaseTest;
import io.restassured.http.ContentType;
import org.junit.Test;
import java.util.HashMap;
import java.util.Map;
import static io.restassured.RestAssured.given;

public class BarrigaTest extends BaseTest {

    @Test
    public void naoDeveAcessarApiSemToken() {
        given()
                .when()
                    .get("/contas")
                .then()
                    .statusCode(401);
    }


}
