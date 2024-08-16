package eventos;

import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.fluid.FluidUpdateVO;
import utilitarios.Utils;

import java.math.BigDecimal;

public class RemoverNufin implements EventoProgramavelJava {
    @Override
    public void beforeInsert(PersistenceEvent persistenceEvent) throws Exception {

    }

    @Override
    public void beforeUpdate(PersistenceEvent persistenceEvent) throws Exception {

    }

    @Override
    public void beforeDelete(PersistenceEvent persistenceEvent) throws Exception {
        DynamicVO tgfFin = (DynamicVO) persistenceEvent.getVo();
        BigDecimal nufin = tgfFin.asBigDecimal("NUFIN");

        DynamicVO buscarNufin = Utils.retornaVO("AD_PGLOCFOLHADET", "NUFIN = " + nufin);
        if (buscarNufin != null) {
            FluidUpdateVO updateVO = Utils.getFluidUpdateByPKVO("AD_PGLOCFOLHADET", buscarNufin.getPrimaryKey());
            updateVO.set("NUFIN", null);
            updateVO.update();
        }
    }

    @Override
    public void afterInsert(PersistenceEvent persistenceEvent) throws Exception {

    }

    @Override
    public void afterUpdate(PersistenceEvent persistenceEvent) throws Exception {

    }

    @Override
    public void afterDelete(PersistenceEvent persistenceEvent) throws Exception {

    }

    @Override
    public void beforeCommit(TransactionContext transactionContext) throws Exception {

    }
}
