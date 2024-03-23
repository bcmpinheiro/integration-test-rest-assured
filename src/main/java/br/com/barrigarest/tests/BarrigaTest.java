package br.com.barrigarest.tests;

import br.com.barrigarest.core.BaseTest;
import br.com.barrigarest.tests.utils.DateUtils;
import io.restassured.RestAssured;
import io.restassured.specification.FilterableRequestSpecification;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.manipulation.Filterable;
import org.junit.runners.MethodSorters;

import java.util.HashMap;
import java.util.Map;
import static io.restassured.RestAssured.given;
import static jdk.dynalink.linker.support.Guards.isNotNull;
import static org.hamcrest.Matchers.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BarrigaTest extends BaseTest {

    private static String CONTA_NAME = "Conta " + System.nanoTime();
    private static Integer CONTA_ID;
    private static Integer MOVE_ID;

    @BeforeClass
    public static void login() {
        Map<String,String> login = new HashMap<>();
        login.put("email", "barbara@barbara");
        login.put("senha", "123456");

        String TOKEN = given()
                .body(login)
                .when()
                .post("/signin")
                .then()
                .statusCode(200)
                .extract().path("token");

        RestAssured.requestSpecification.header("Authorization", "JWT " + TOKEN);
    }

    @Test
    public void t01_naoDeveAcessarApiSemToken() {
        given()
                .when()
                    .get("/contas")
                .then()
                    .statusCode(200);
    }

    @Test
    public void t02_deveIncluirContaComSucesso() {
        CONTA_ID = given()
                    .body("{\"nome\": \""+CONTA_NAME+"\"}")
                .when()
                    .post("/contas")
                .then()
                    .statusCode(201)
                    .extract().path("id");
    }

    @Test
    public void t03_deveAlterarContaComSucesso() {
        given()
                    .body("{ \"nome\": \""+CONTA_NAME+" alterada\" }")
                    .pathParam("id", CONTA_ID)
                .when()
                    .put("/contas/{id}")
                .then()
                    .statusCode(200)
                    .body("nome", is(CONTA_NAME+" alterada"));
    }

    @Test
    public void t04_naoDeveInserirContaComMesmoNome() {
        given()
                    .body("{ \"nome\": \""+CONTA_NAME+" alterada\" }")
                .when()
                    .post("/contas")
                .then()
                    .statusCode(400)
                    .body("error", is("Já existe uma conta com esse nome!"));
    }

    @Test
    public void t05_deveInserirUmaMovimentacaoComSucesso() {
        Movimentacao movimentacao = getMovimentacaoValida();

        MOVE_ID = given()
                    .body(movimentacao)
                .when()
                    .post("/transacoes")
                .then()
                    .statusCode(201)
                    .extract().path("id");
    }

    @Test
    public void t06_deveValidarCamposObrigatoriosDaMovimentacao() {
        given()
                    .body("{}")
                .when()
                    .post("/transacoes")
                .then()
                    .statusCode(400)
                    .body("$", hasSize(8))
                    .body("msg", hasItems(
                            "Data da Movimentação é obrigatório",
                            "Data do pagamento é obrigatório",
                            "Descrição é obrigatório",
                            "Interessado é obrigatório",
                            "Valor é obrigatório",
                            "Valor deve ser um número",
                            "Conta é obrigatório",
                            "Situação é obrigatório"));
    }

    @Test
    public void t07_naoDeveInserirMovimentacaoFutura() {
        Movimentacao movimentacao = getMovimentacaoValida();
        movimentacao.setData_transacao(DateUtils.getDataDiferencaDeDias(2));

        given()
                    .body(movimentacao)
                .when()
                    .post("/transacoes")
                .then()
                    .statusCode(400)
                    .body("$", hasSize(1))
                    .body("msg", hasItems("Data da Movimentação deve ser menor ou igual à data atual"));
    }

    @Test
    public void t08_naoDeveRemoverContaQuePossuiMovimentacao() {
        given()
                    .pathParam("id", CONTA_ID)
                .when()
                    .delete("/contas/{id}")
                .then()
                    .statusCode(500)
                    .body("constraint", is("transacoes_conta_id_foreign"));
    }

    @Test
    public void t09_deveCalcularSaldoContas() {
        given()
                .when()
                    .get("/saldo")
                .then()
                    .statusCode(200)
                    .body("find{it.conta_id == "+CONTA_ID+"}.saldo", is("100.00"));
    }

    @Test
    public void t10_deveRemoverMovimentacao() {
        given()
                    .pathParam("id", MOVE_ID)
                .when()
                    .delete("/transacoes/{id}")
                .then()
                    .statusCode(204);
    }

    @Test
    public void t11_naoDeveAcessarApiSemToken() {
        FilterableRequestSpecification requestSpecification = (FilterableRequestSpecification) RestAssured.requestSpecification;
        requestSpecification.removeHeader("Authorization");

        given()
                .when()
                .get("/contas")
                .then()
                .statusCode(401);
    }

    private Movimentacao getMovimentacaoValida() {
        Movimentacao movimentacao = new Movimentacao();
        movimentacao.setConta_id(CONTA_ID);
        //movimentacao.setUsuario_id();
        movimentacao.setDescricao("descricao da movimentacao");
        movimentacao.setEnvolvido("envolvido na movimentacao");
        movimentacao.setTipo("REC");
        movimentacao.setData_transacao(DateUtils.getDataDiferencaDeDias(-1));
        movimentacao.setData_pagamento(DateUtils.getDataDiferencaDeDias(5));
        movimentacao.setValor(100f);
        movimentacao.setStatus(true);
        return movimentacao;
    }
}
