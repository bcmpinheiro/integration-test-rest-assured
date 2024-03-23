package br.com.barrigarest.tests.refact;

import br.com.barrigarest.core.BaseTest;
import br.com.barrigarest.tests.Movimentacao;
import br.com.barrigarest.tests.utils.BarrigaUtils;
import br.com.barrigarest.tests.utils.DataUtils;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class MovimentacaoTest extends BaseTest {

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
        Integer CONTA_ID = BarrigaUtils.getIdContaPeloNome("Conta com movimentacao");

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
        Integer MOVE_ID = BarrigaUtils.getIdMovimentacaoPelaDescricao("Movimentacao para exclusao");

        given()
                .pathParam("id", MOVE_ID)
                .when()
                .delete("/transacoes/{id}")
                .then()
                .statusCode(204);
    }


    private Movimentacao getMovimentacaoValida() {
        Movimentacao movimentacao = new Movimentacao();
        movimentacao.setConta_id(BarrigaUtils.getIdContaPeloNome("Conta para movimentacoes"));
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
