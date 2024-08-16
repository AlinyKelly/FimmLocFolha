package ce.br.com.sankhya.fimm.pag.loc.fol.botoes;

import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import utilitarios.Utils;

import java.math.BigDecimal;
import java.util.Collection;

public class AtualizarParceiros implements AcaoRotinaJava {
    @Override
    public void doAction(ContextoAcao contextoAcao) throws Exception {

        if (contextoAcao.confirmarSimNao("Atualizar Parceiros", "Deseja atualizar apenas as linhas selecionadas?\n*Para atualizar toda a grade selecione (NÃO).", 1)) {
            Registro[] linhasSelecionadas = contextoAcao.getLinhas();
            for (Registro linha : linhasSelecionadas) {
                BigDecimal codparc = (BigDecimal) linha.getCampo("CODPARC");

                //Buscar na TGFPAR os campos para atualizar na tabela
                DynamicVO buscarParceiro = Utils.retornaVO("Parceiro", "CODPARC = " + codparc);

                BigDecimal bancoParceiro = buscarParceiro.asBigDecimal("CODBCO");
                String contaParceiro = buscarParceiro.asString("CODCTABCO");
                String digitoContaParceiro = buscarParceiro.asString("AD_DIGCONTAPARC");
                String tipoContaParceiro = buscarParceiro.asString("AD_TIPOCONTA");
                BigDecimal centroResultadoParceiro = buscarParceiro.asBigDecimal("AD_CODCENCUS");

                linha.setCampo("CODBCO", bancoParceiro); //banco do parceiro
                linha.setCampo("CODCTABCO", contaParceiro); //conta do parceiro
                linha.setCampo("DIGCONTAPARC", digitoContaParceiro); //digito da conta do parceiro
                linha.setCampo("TIPOCONTA", tipoContaParceiro); //tipo da conta do parceiro
                linha.setCampo("CODCENCUS", centroResultadoParceiro); //centro de resultados do parceiro
                linha.setCampo("ERRO",null);
                linha.save();
            }
        } else {

            //Buscar cada parceiro da grade e atualizar a linha individualmente
            Registro linhaPai = contextoAcao.getLinhaPai();
            Object codpg = linhaPai.getCampo("CODPG");

            Collection<DynamicVO> buscarLinhas = Utils.retornaVOs("AD_PGLOCFOLHADET", "CODPG = " + codpg);

            for (DynamicVO updateParceiro : buscarLinhas) {
                BigDecimal codDetalhe = updateParceiro.asBigDecimal("CODPGDET");
                BigDecimal codparc = updateParceiro.asBigDecimal("CODPARC");

                //Buscar na TGFPAR os campos para atualizar na tabela
                DynamicVO buscarParceiro = Utils.retornaVO("Parceiro", "CODPARC = " + codparc);

                BigDecimal bancoParceiro = buscarParceiro.asBigDecimal("CODBCO");
                String contaParceiro = buscarParceiro.asString("CODCTABCO");
                String digitoContaParceiro = buscarParceiro.asString("AD_DIGCONTAPARC");
                String tipoContaParceiro = buscarParceiro.asString("AD_TIPOCONTA");
                BigDecimal centroResultadoParceiro = buscarParceiro.asBigDecimal("AD_CODCENCUS");

                JapeSession.SessionHandle hnd = null;
                try {
                    hnd = JapeSession.open();

                    JapeFactory.dao("AD_PGLOCFOLHADET")
                            .prepareToUpdateByPK(codpg, codDetalhe)
                            .set("CODBCO", bancoParceiro) //banco do parceiro
                            .set("CODCTABCO", contaParceiro) //conta do parceiro
                            .set("DIGCONTAPARC", digitoContaParceiro) //digito da conta do parceiro
                            .set("TIPOCONTA", tipoContaParceiro) //tipo da conta do parceiro
                            .set("CODCENCUS", centroResultadoParceiro) //centro de resultados do parceiro
                            .set("ERRO", null)
                            .update();

                } catch (Exception e) {
                    throw new Exception(e);
                } finally {
                    JapeSession.close(hnd);
                }
            }
        }

    }
}
