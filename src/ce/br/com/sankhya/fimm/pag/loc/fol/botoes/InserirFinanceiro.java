package ce.br.com.sankhya.fimm.pag.loc.fol.botoes;

import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jape.vo.DynamicVO;
import utilitarios.Utils;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class InserirFinanceiro implements AcaoRotinaJava {
    @Override
    public void doAction(ContextoAcao contextoAcao) throws Exception {
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
            String tipoConta = (String) linha.getCampo("TIPOCONTA");
            BigDecimal centroResultado = (BigDecimal) linha.getCampo("CODCENCUS");

            LocalDateTime localDateTime = dtpagamento.toLocalDateTime();
            int mes = localDateTime.getMonthValue() - 1;
            int ano = localDateTime.getYear();

            String numnota = mes + String.valueOf(ano);

            //Utils.mostraErro("Competencia = " + numnota + " Data Pagamento = " + dtpagamento + " Mes - 1 = " + mes + " ano = " + ano + " Numnota = " + numnota);

            BigDecimal tipoTitulo;
            DynamicVO buscarConfigTipo = Utils.retornaVO("AD_CONFIGTIP", "TIPOCONTA = '" + tipoConta + "'");

            if (buscarConfigTipo != null) {
                tipoTitulo = buscarConfigTipo.asBigDecimalOrZero("CODTIPTIT");
            } else {
                tipoTitulo = BigDecimal.ZERO;
            }

            Registro criarFinanceiro = contextoAcao.novaLinha("Financeiro");
            criarFinanceiro.setCampo("NUMNOTA", numnota);
            criarFinanceiro.setCampo("CODPARC", parceiro);
            criarFinanceiro.setCampo("CODEMP", empresaPagamento);
            criarFinanceiro.setCampo("RECDESP", -1);
            criarFinanceiro.setCampo("DTVENC", dtVencimento);
            criarFinanceiro.setCampo("DTNEG", dtNeg);
            criarFinanceiro.setCampo("DHMOV", dtNeg); //Data e Hora
            criarFinanceiro.setCampo("DTENTSAI", dtNeg);
            criarFinanceiro.setCampo("DTALTER", Utils.getDHAtual()); //Data e Hora
            criarFinanceiro.setCampo("VLRDESDOB", vlrpag);
            criarFinanceiro.setCampo("CODTIPOPER", top);
            criarFinanceiro.setCampo("CODTIPTIT", tipoTitulo);
            criarFinanceiro.setCampo("CODNAT", natureza);
            criarFinanceiro.setCampo("CODCENCUS", centroResultado);
            criarFinanceiro.setCampo("CODBCO", bancoPagamento);
            criarFinanceiro.setCampo("CODCTABCOINT", contaPagamento);
            criarFinanceiro.setCampo("AD_DTPAG", dtpagamento);
            criarFinanceiro.save();

            Object nufin = criarFinanceiro.getCampo("NUFIN");

            //Realizar um update no campo nufin das linhas selecionadas.
            linha.setCampo("NUFIN", nufin);
            linha.save();
        }

        contextoAcao.setMensagemRetorno("Títulos gerados com sucesso.");

    }
}
