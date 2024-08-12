package ce.br.com.sankhya.fimm.pag.loc.fol.botoes

import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava
import br.com.sankhya.extensions.actionbutton.ContextoAcao
import br.com.sankhya.jape.core.JapeSession
import br.com.sankhya.modelcore.MGEModelException
import br.com.sankhya.ws.ServiceContext
import org.apache.commons.io.FileUtils
import utilitarios.Utils
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.math.BigDecimal
import java.util.regex.Matcher
import java.util.regex.Pattern

class ImportadorCsvKt : AcaoRotinaJava {
    private var codImportador: BigDecimal? = null

    override fun doAction(contextoAcao: ContextoAcao) {
        var hnd: JapeSession.SessionHandle? = null

        var ultimaLinhaJson: LinhaJson? = null

        val linhasSelecionadas  = contextoAcao.linhas

        try {
            for (linha in linhasSelecionadas) {
                var count = 0

                codImportador = linha.getCampo("CODPG") as BigDecimal?
                val data = linha.getCampo("ARQUIVO") as ByteArray?
                val ctx = ServiceContext.getCurrent()
                val file = File(ctx.tempFolder, "IMPPG" + System.currentTimeMillis())
                FileUtils.writeByteArrayToFile(file, data)

                hnd = JapeSession.open()

                BufferedReader(FileReader(file)).use { br ->
                    var line = br.readLine()

                    while (line != null) {
                        if (count == 0) {
                            count++
                            line = br.readLine()
                            continue
                        }
                        count++

                        if (line.contains("__end_fileinformation__")) {
                            line = getReplaceFileInfo(line)
                        }

                        val json = trataLinha(line)
                        ultimaLinhaJson = json

                        val codparc = json.codparc.trim().toBigDecimal()

                        val buscarParceiro = Utils.retornaVO("Parceiro", "CODPARC = $codparc")

                        if (buscarParceiro != null) {
                            val bancoParceiro = buscarParceiro.asBigDecimal("CODBCO")
                            val contaParceiro = buscarParceiro.asString("CODCTABCO")
                            val digitoContaParceiro = buscarParceiro.asString("AD_DIGCONTAPARC")
                            val tipoContaParceiro = buscarParceiro.asString("AD_TIPOCONTA")
                            val centroResultadoParceiro = buscarParceiro.asBigDecimalOrZero("AD_CODCENCUS")

                            val novoDetalhe = contextoAcao.novaLinha("AD_PGLOCFOLHADET")
                            novoDetalhe.setCampo("CODPG", codImportador)
                            novoDetalhe.setCampo("CODPARC", codparc)
                            novoDetalhe.setCampo("VLRPAG", json.valor.trim().toBigDecimal())
                            novoDetalhe.setCampo("CODBCO", bancoParceiro) //banco do parceiro
                            novoDetalhe.setCampo("CODCTABCO", contaParceiro) //conta do parceiro
                            novoDetalhe.setCampo("DIGCONTAPARC", digitoContaParceiro) //digito da conta do parceiro
                            novoDetalhe.setCampo("TIPOCONTA", tipoContaParceiro) //tipo da conta do parceiro
                            novoDetalhe.setCampo("CODCENCUS", centroResultadoParceiro) //centro de resultados do parceiro
                            novoDetalhe.save()

                            line = br.readLine()
                        }

                    }
                }
            }

        } catch (e: Exception) {
            throw MGEModelException("$e $ultimaLinhaJson ")
        } finally {
            JapeSession.close(hnd)
        }
    }

    private fun getReplaceFileInfo(line: String): String {
        val regex = "__start_fileinformation__.*__end_fileinformation__"
        val subst = ""

        val pattern: Pattern = Pattern.compile(regex, Pattern.MULTILINE)
        val matcher: Matcher = pattern.matcher(line)

        return matcher.replaceAll(subst)
    }

    private fun trataLinha(linha: String): LinhaJson {
        var cells = if (linha.contains(";")) linha.split(";(?=([^\"]*\"[^\"]*\")*[^\"]*\$)".toRegex()).toTypedArray()
        else linha.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*\$)".toRegex()).toTypedArray()

        cells = cells.filter { predicate ->
            if (predicate.isEmpty())
                return@filter false
            return@filter true
        }.toTypedArray() // Remove linhas vazias

        val ret = if (cells.isNotEmpty()) LinhaJson(
            cells[0],
            cells[1]
        ) else
            null

        if (ret == null) {
            throw Exception("Erro ao processar a linha: $linha")
        }

        return ret

    }

    data class LinhaJson(
        val codparc: String,
        val valor: String
    )
}