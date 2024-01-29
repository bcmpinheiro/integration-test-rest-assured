import io.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.junit.Test;

public class UserJsonTest {

    @Test
    public void deveVerificarPrimeiroNivel() {
        RestAssured.given()
                .when()
                    .get("https://restapi.wcaquino.me/users/1")
                .then()
                    .statusCode(200)
                    .body("id", Matchers.is(1))
                    .body("name", Matchers.containsString("Silva"))
                    .body("age", Matchers.greaterThan(18));
    }
}
