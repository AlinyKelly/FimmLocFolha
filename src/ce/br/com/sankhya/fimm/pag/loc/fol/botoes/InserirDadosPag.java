package ce.br.com.sankhya.fimm.pag.loc.fol.botoes;

import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jape.vo.DynamicVO;
import utilitarios.Utils;

import java.math.BigDecimal;

public class InserirDadosPag implements AcaoRotinaJava {
    @Override
    public void doAction(ContextoAcao contextoAcao) throws Exception {
        Object bancoParametro = contextoAcao.getParam("CODBANCO");
        Object contaParametro = contextoAcao.getParam("CODCONTA");
        Object dtvencParametro = contextoAcao.getParam("DTVENC");
        Object dtnegParametro = contextoAcao.getParam("DTNEG");
        Object dtpagParametro = contextoAcao.getParam("DTPAG");
        Object topParametro = contextoAcao.getParam("TOP");
        Object naturezaParametro = contextoAcao.getParam("NATUREZA");

        Registro[] linhasSelecionadas  = contextoAcao.getLinhas();

        for (Registro linha : linhasSelecionadas) {

            DynamicVO buscarEmpresa = Utils.retornaVO("ContaBancaria", "CODCTABCOINT = " + contaParametro);
            BigDecimal empresaPagamento = buscarEmpresa.asBigDecimalOrZero("CODEMP");

            //Realizar o UPDATE nas linhas selecionadas
            linha.setCampo("CODBCOPG", bancoParametro);
            linha.setCampo("CODCTABCOPG", contaParametro);
            linha.setCampo("DTVENC", dtvencParametro);
            linha.setCampo("DTNEG", dtnegParametro);
            linha.setCampo("DTPAG", dtpagParametro);
            linha.setCampo("CODTIPOPER", topParametro);
            linha.setCampo("CODNAT", naturezaParametro);
            linha.setCampo("CODEMPPG", empresaPagamento);
            linha.save();
        }
    }
}
