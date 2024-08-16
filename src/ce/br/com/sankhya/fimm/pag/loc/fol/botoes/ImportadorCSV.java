package ce.br.com.sankhya.fimm.pag.loc.fol.botoes;

import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.modelcore.MGEModelException;
import br.com.sankhya.ws.ServiceContext;
import org.apache.commons.io.FileUtils;
import utilitarios.Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ImportadorCSV implements AcaoRotinaJava {
    private Object codImportador;

    @Override
    public void doAction(ContextoAcao contextoAcao) throws Exception {

        JapeSession.SessionHandle hnd = null;
        LinhaJson ultimaLinhaJson = null;

        Registro[] linhasSelecionadas = contextoAcao.getLinhas();

        try {
            for (Registro linha : linhasSelecionadas) {
                int count = 0;

                codImportador = linha.getCampo("CODPG");
                byte[] data = (byte[]) linha.getCampo("ARQUIVO");
                ServiceContext ctx = ServiceContext.getCurrent();
                File file = new File(ctx.getTempFolder(), "IMPPG" + System.currentTimeMillis());
                FileUtils.writeByteArrayToFile(file, data);

                hnd = JapeSession.open();

                try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                    String line = br.readLine();

                    while (line != null) {
                        if (count == 0) {
                            count++;
                            line = br.readLine();
                            continue;
                        }
                        count++;

                        if (line.contains("__end_fileinformation__")) {
                            line = getReplaceFileInfo(line);
                        }

                        LinhaJson json = trataLinha(line);
                        ultimaLinhaJson = json;

                        BigDecimal codparc = new BigDecimal(json.codparc.trim());

                        DynamicVO buscarParceiro = Utils.retornaVO("Parceiro", "CODPARC = " + codparc);
                        if (buscarParceiro != null) {
                            BigDecimal bancoParceiro = buscarParceiro.asBigDecimal("CODBCO");
                            String contaParceiro = buscarParceiro.asString("CODCTABCO");
                            String digitoContaParceiro = buscarParceiro.asString("AD_DIGCONTAPARC");
                            String tipoContaParceiro = buscarParceiro.asString("AD_TIPOCONTA");
                            BigDecimal centroResultadoParceiro = buscarParceiro.asBigDecimal("AD_CODCENCUS");

                            Registro novoDetalhe = contextoAcao.novaLinha("AD_PGLOCFOLHADET");
                            novoDetalhe.setCampo("CODPG", codImportador);
                            novoDetalhe.setCampo("CODPARC", codparc);
                            novoDetalhe.setCampo("VLRPAG", converterValorMonetario(json.valor.trim()));
                            novoDetalhe.setCampo("CODBCO", bancoParceiro); //banco do parceiro
                            novoDetalhe.setCampo("CODCTABCO", contaParceiro); //conta do parceiro
                            novoDetalhe.setCampo("DIGCONTAPARC", digitoContaParceiro); //digito da conta do parceiro
                            novoDetalhe.setCampo("TIPOCONTA", tipoContaParceiro); //tipo da conta do parceiro
                            novoDetalhe.setCampo("CODCENCUS", centroResultadoParceiro); //centro de resultados do parceiro

                            novoDetalhe.save();

                            line = br.readLine();
                        }
                    }
                }
            }

        } catch (Exception e) {
            throw new MGEModelException(e + " " + ultimaLinhaJson);
        } finally {
            JapeSession.close(hnd);
        }
    }

    private String getReplaceFileInfo(String line) {
        String regex = "__start_fileinformation__.*__end_fileinformation__";
        String subst = "";

        Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(line);

        return matcher.replaceAll(subst);
    }

    private LinhaJson trataLinha(String linha) throws Exception {
        String[] cells;
        if (linha.contains(";")) {
            // cells = linha.split("(?<=([^\"]*\"[^\"]*\")*[^\"]*$);");
            cells = linha.split(";(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
        } else {
//            cells = linha.split("(?<=([^\"]*\"[^\"]*\")*[^\"]*$),");
            cells = linha.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
        }

        cells = java.util.Arrays.stream(cells)
                .filter(predicate -> !predicate.isEmpty())
                .toArray(String[]::new);

        LinhaJson ret;
        if (cells.length > 0) {
            ret = new LinhaJson(cells[0], cells[1]);
        } else {
            ret = null;
        }

        if (ret == null) {
            throw new Exception("Erro ao processar a linha: " + linha);
        }

        return ret;
    }

    private BigDecimal converterValorMonetario(String valorMonetario) {
        String valorNumerico = valorMonetario.replace("\"","").replace(".", "").replace(",", ".");
        return new BigDecimal(valorNumerico);
    }

    public static class LinhaJson {
        public final String codparc;
        public final String valor;

        public LinhaJson(String codparc, String valor) {
            this.codparc = codparc;
            this.valor = valor;
        }
    }
}