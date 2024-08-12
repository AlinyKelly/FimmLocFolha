package ce.br.com.sankhya.fimm.pag.loc.fol.botoes;

import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jape.vo.DynamicVO;
import utilitarios.Utils;

import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;

public class InserirFinanceiro implements AcaoRotinaJava {
    @Override
    public void doAction(ContextoAcao contextoAcao) throws Exception {
        // Pegar a empresa para a TGFFIN da Conta do Pagamento
        // DTENTSAI, DHMOV usar igual à DTNEG, DTALTER pegar a data do servidor
        // Usar NUMNOTA sequencial
        // Fixar RECDESP = -1

        Registro[] linhasSelecionadas = contextoAcao.getLinhas();

        for (Registro linha : linhasSelecionadas) {
            BigDecimal parceiro = (BigDecimal) linha.getCampo("CODPARC");
            BigDecimal vlrpag = (BigDecimal) linha.getCampo("VLRPAG");
            BigDecimal empresaPagamento = (BigDecimal) linha.getCampo("CODEMPPG");
            BigDecimal bancoPagamento = (BigDecimal) linha.getCampo("CODBCOPG");
            String contaPagamento = (String) linha.getCampo("CODCTABCOPG");
            Timestamp dtVencimento = (Timestamp) linha.getCampo("DTVENC");
            Timestamp dtNeg = (Timestamp) linha.getCampo("DTNEG");
            Timestamp dtpagamento = (Timestamp) linha.getCampo("DTPAG");
            BigDecimal top = (BigDecimal) linha.getCampo("CODTIPOPER");
            BigDecimal natureza = (BigDecimal) linha.getCampo("CODNAT");

            //Criar a TGFFIN

        }
    }
}
