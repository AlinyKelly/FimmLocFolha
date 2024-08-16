package utilitarios;

import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.jape.wrapper.fluid.FluidCreateVO;
import br.com.sankhya.jape.wrapper.fluid.FluidUpdateVO;
import br.com.sankhya.modelcore.MGEModelException;

import java.sql.Timestamp;
import java.util.Collection;

public class Utils {

    public static DynamicVO retornaVO(String instancia, String criteria) throws Exception {

        JapeSession.SessionHandle hnd = null;
        try {
            hnd = JapeSession.open();

            JapeWrapper dao = JapeFactory.dao(instancia);

            return dao.findOne(criteria);

        } catch (
                Exception e
        ) {
            throw new Exception(e);
        } finally {
            JapeSession.close(hnd);
        }


    }


    public static Collection<DynamicVO> retornaVOs(String instancia, String criteria) throws Exception {

        JapeSession.SessionHandle hnd = null;
        try {
            hnd = JapeSession.open();

            JapeWrapper dao = JapeFactory.dao(instancia);

            return dao.find(criteria);

        } catch (
                Exception e
        ) {
            throw new Exception(e);
        } finally {
            JapeSession.close(hnd);
        }

    }

    public static FluidCreateVO getFluidCreateVO(String instancia) throws MGEModelException {
        JapeSession.SessionHandle hnd = null;
        FluidCreateVO vo;
        try {
            hnd = JapeSession.open();

            JapeWrapper separacaoDAO = JapeFactory.dao(instancia);
            vo = separacaoDAO.create();
        } catch (Exception e) {
            throw new MGEModelException(e);
        } finally {
            JapeSession.close(hnd);
        }
        return vo;
    }

    public static FluidUpdateVO getFluidUpdateByPKVO(String instancia, Object... objects) throws MGEModelException {
        JapeSession.SessionHandle hnd = null;
        FluidUpdateVO vo;
        try {
            hnd = JapeSession.open();

            JapeWrapper instanciaDAO = JapeFactory.dao(instancia);
            vo = instanciaDAO.prepareToUpdateByPK(objects);
        } catch (Exception e) {
            throw new MGEModelException(e);
        } finally {
            JapeSession.close(hnd);
        }
        return vo;
    }

    public static void mostraErro(String message) throws Exception {
        throw new Exception(message);
    }

    public static Timestamp getDHAtual() {
        return new Timestamp(System.currentTimeMillis());
    }
}

