package br.com.barrigarest.tests;

import br.com.barrigarest.core.BaseTest;
import org.junit.Before;
import org.junit.Test;
import java.util.HashMap;
import java.util.Map;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class BarrigaTest extends BaseTest {

    private String TOKEN;
    @Before
    public void login() {
        Map<String,String> login = new HashMap<>();
        login.put("email", "barbara@barbara");
        login.put("senha", "123456");

        TOKEN = given()
                .body(login)
                .when()
                .post("/signin")
                .then()
                .statusCode(200)
                .extract().path("token");
    }

    @Test
    public void naoDeveAcessarApiSemToken() {
        given()
                .when()
                    .get("/contas")
                .then()
                    .statusCode(401);
    }

    @Test
    public void deveIncluirContaComSucesso() {
        given()
                    .header("Authorization", "JWT " + TOKEN)
                    .body("{\"nome\": \"teste2\"}")
                .when()
                    .post("/contas")
                .then()
                    .statusCode(201);
    }

    @Test
    public void deveAlterarContaComSucesso() {
        given()
                    .header("Authorization", "JWT " + TOKEN)
                    .body("{\"nome\": \"conta teste n1\"}")
                .when()
                    .put("/contas/2041183")
                .then()
                    .statusCode(200)
                    .body("nome", is("conta teste n1"));
    }

    @Test
    public void naoDeveInserirContaComMesmoNome() {
        given()
                    .header("Authorization", "JWT " + TOKEN)
                    .body("{\"nome\": \"conta teste n2\"}")
                .when()
                    .post("/contas")
                .then()
                    .statusCode(400)
                    .body("error", is("Já existe uma conta com esse nome!"));
    }

    @Test
    public void deveInserirUmaMovimentacaoComSucesso() {
        Movimentacao movimentacao = new Movimentacao();
        movimentacao.setConta_id(2041183);
        //movimentacao.setUsuario_id();
        movimentacao.setDescricao("descricao da movimentacao");
        movimentacao.setEnvolvido("envolvido na movimentacao");
        movimentacao.setTipo("REC");
        movimentacao.setData_transacao("01/02/2024");
        movimentacao.setData_pagamento("05/02/2024");
        movimentacao.setValor(100f);
        movimentacao.setStatus(true);

        given()
                    .header("Authorization", "JWT " + TOKEN)
                    .body(movimentacao)
                .when()
                    .post("/transacoes")
                .then()
                    .statusCode(201);
    }
}
