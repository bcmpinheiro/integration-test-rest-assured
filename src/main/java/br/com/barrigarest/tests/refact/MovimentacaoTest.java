package br.com.barrigarest.tests.refact;

import br.com.barrigarest.core.BaseTest;
import br.com.barrigarest.tests.Movimentacao;
import br.com.barrigarest.tests.utils.DataUtils;
import io.restassured.RestAssured;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class MovimentacaoTest extends BaseTest {

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

        get("/reset").then().statusCode(200);
    }

    @Test
    public void deveInserirUmaMovimentacaoComSucesso() {
        Movimentacao movimentacao = getMovimentacaoValida();

        given()
                .body(movimentacao)
                .when()
                .post("/transacoes")
                .then()
                .statusCode(201);
    }

    @Test
    public void deveValidarCamposObrigatoriosDaMovimentacao() {
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
    public void naoDeveInserirMovimentacaoFutura() {
        Movimentacao movimentacao = getMovimentacaoValida();
        movimentacao.setData_transacao(DataUtils.getDataDiferencaDeDias(2));

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
    public void naoDeveRemoverContaQuePossuiMovimentacao() {
        Integer CONTA_ID = getIdContaPeloNome("Conta com movimentacao");

        given()
                .pathParam("id", CONTA_ID)
                .when()
                .delete("/contas/{id}")
                .then()
                .statusCode(500)
                .body("constraint", is("transacoes_conta_id_foreign"));
    }

    @Test
    public void deveRemoverMovimentacao() {
        Integer MOVE_ID = getIdMovimentacaoPelaDescricao("Movimentacao para exclusao");

        given()
                .pathParam("id", MOVE_ID)
                .when()
                .delete("/transacoes/{id}")
                .then()
                .statusCode(204);
    }

    public Integer getIdContaPeloNome(String nome) {
        return RestAssured.get("/contas?nome="+nome).then().extract().path("id[0]");
    }

    public Integer getIdMovimentacaoPelaDescricao(String desc) {
        return RestAssured.get("/transacoes?descricao="+desc).then().extract().path("id[0]");
    }

    private Movimentacao getMovimentacaoValida() {
        Movimentacao movimentacao = new Movimentacao();
        movimentacao.setConta_id(getIdContaPeloNome("Conta para movimentacoes"));
        //movimentacao.setUsuario_id();
        movimentacao.setDescricao("descricao da movimentacao");
        movimentacao.setEnvolvido("envolvido na movimentacao");
        movimentacao.setTipo("REC");
        movimentacao.setData_transacao(DataUtils.getDataDiferencaDeDias(-1));
        movimentacao.setData_pagamento(DataUtils.getDataDiferencaDeDias(5));
        movimentacao.setValor(100f);
        movimentacao.setStatus(true);
        return movimentacao;
    }

}
