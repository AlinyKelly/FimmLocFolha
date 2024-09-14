package ce.br.com.sankhya.fimm.pag.loc.fol.botoes;

import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import utilitarios.Utils;

import java.math.BigDecimal;

import static com.jidesoft.swing.JideSwingUtilities.throwException;

//Inseri os dados para todas as linhas da tabela detalhe

public class InserirDadosPagAll implements AcaoRotinaJava {
    @Override
    public void doAction(ContextoAcao contextoAcao) throws Exception {
        Object bancoParametro = contextoAcao.getParam("CODBANCO");
        Object contaParametro = contextoAcao.getParam("CODCONTA");
        Object dtvencParametro = contextoAcao.getParam("DTVENC");
        Object dtnegParametro = contextoAcao.getParam("DTNEG");
        Object dtpagParametro = contextoAcao.getParam("DTPAG");
        Object topParametro = contextoAcao.getParam("TOP");
        Object naturezaParametro = contextoAcao.getParam("NATUREZA");
        Object centroResultParametro = contextoAcao.getParam("CENTRORESULTADOS");

        Registro linhaPai = contextoAcao.getLinhaPai();
        Object codpg = linhaPai.getCampo("CODPG");

        DynamicVO buscarEmpresa = Utils.retornaVO("ContaBancaria", "CODCTABCOINT = " + contaParametro);
        BigDecimal empresaPagamento = buscarEmpresa.asBigDecimalOrZero("CODEMP");

        DynamicVO detalhes = Utils.retornaVO("AD_PGLOCFOLHADET", "CODPG = " + codpg);

        JapeSession.SessionHandle hnd = null;

        try {
            hnd = JapeSession.open();

            JapeFactory.dao("AD_PGLOCFOLHADET")
                    .prepareToUpdate(detalhes)
                    .set("CODBCOPG", bancoParametro)
                    .set("CODCTABCOPG", contaParametro)
                    .set("DTVENC", dtvencParametro)
                    .set("DTNEG", dtnegParametro)
                    .set("DTPAG", dtpagParametro)
                    .set("CODTIPOPER", topParametro)
                    .set("CODNAT", naturezaParametro)
                    .set("CODCENCUS", centroResultParametro)
                    .set("CODEMPPG", empresaPagamento)
                    .update();

        }  catch (Exception e) {
            throw new Exception(e);
        } finally {
            JapeSession.close(hnd);
        }

    }
}
