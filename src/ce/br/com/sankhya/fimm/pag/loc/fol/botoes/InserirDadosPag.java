package ce.br.com.sankhya.fimm.pag.loc.fol.botoes;

import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import utilitarios.Utils;

import java.math.BigDecimal;
import java.util.Collection;

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
        Object inserirParaTodasLinhas = contextoAcao.getParam("TODASLINHAS");

        DynamicVO buscarEmpresa = Utils.retornaVO("ContaBancaria", "CODCTABCOINT = " + contaParametro);
        BigDecimal empresaPagamento = buscarEmpresa.asBigDecimalOrZero("CODEMP");

        if (inserirParaTodasLinhas.equals("S")) {
            Registro linhaPai = contextoAcao.getLinhaPai();
            Object codpg = linhaPai.getCampo("CODPG");

            JapeSession.SessionHandle hnd = null;
            JdbcWrapper jdbc = null;

            try {
                hnd = JapeSession.open();
                jdbc = EntityFacadeFactory.getDWFFacade().getJdbcWrapper();
                jdbc.openSession();

                NativeSql queryUpd = new NativeSql(jdbc);
                queryUpd.appendSql("UPDATE AD_PGLOCFOLHADET SET CODBCOPG = :CODBCOPG, CODCTABCOPG = :CODCTABCOPG, DTVENC = :DTVENC, DTNEG = :DTNEG, DTPAG = :DTPAG, CODTIPOPER = :CODTIPOPER, CODNAT = :CODNAT, CODEMPPG = :CODEMPPG WHERE CODBCOPG IS NULL AND CODCTABCOPG IS NULL AND DTVENC IS NULL AND DTNEG IS NULL AND DTPAG IS NULL AND CODTIPOPER IS NULL AND CODNAT IS NULL AND CODEMPPG  IS NULL AND CODPG = :CODPG");
                //queryUpd.executeUpdate();
                queryUpd.setReuseStatements(true); // Utilizado para consumir menos memoria
                queryUpd.setBatchUpdateSize(500); // A cada 500 updates armazenados, será feito um commit (flush) do comando.

                queryUpd.setNamedParameter("CODBCOPG", new BigDecimal(String.valueOf(bancoParametro)));
                queryUpd.setNamedParameter("CODCTABCOPG", contaParametro);
                queryUpd.setNamedParameter("DTVENC", dtvencParametro);
                queryUpd.setNamedParameter("DTNEG", dtnegParametro);
                queryUpd.setNamedParameter("DTPAG", dtpagParametro);
                queryUpd.setNamedParameter("CODTIPOPER", new BigDecimal(String.valueOf(topParametro)));
                queryUpd.setNamedParameter("CODNAT", new BigDecimal(String.valueOf(naturezaParametro)));
                queryUpd.setNamedParameter("CODEMPPG", empresaPagamento);
                queryUpd.setNamedParameter("CODPG", codpg);
                queryUpd.addBatch();
                queryUpd.cleanParameters();

                queryUpd.flushBatchTail();
                NativeSql.releaseResources(queryUpd);

            }  catch (Exception e) {
                throw new Exception(e);
            } finally {
                JdbcWrapper.closeSession(jdbc);
                JapeSession.close(hnd);
            }

        } else {

            Registro[] linhasSelecionadas = contextoAcao.getLinhas();

            for (Registro linha : linhasSelecionadas) {
                //Realizar o UPDATE nas linhas selecionadas
                linha.setCampo("CODBCOPG", new BigDecimal(String.valueOf(bancoParametro)));
                linha.setCampo("CODCTABCOPG", contaParametro);
                linha.setCampo("DTVENC", dtvencParametro);
                linha.setCampo("DTNEG", dtnegParametro);
                linha.setCampo("DTPAG", dtpagParametro);
                linha.setCampo("CODTIPOPER", new BigDecimal(String.valueOf(topParametro)));
                linha.setCampo("CODNAT", new BigDecimal(String.valueOf(naturezaParametro)));
                linha.setCampo("CODEMPPG", empresaPagamento);
                linha.save();
            }
        }
    }
}
