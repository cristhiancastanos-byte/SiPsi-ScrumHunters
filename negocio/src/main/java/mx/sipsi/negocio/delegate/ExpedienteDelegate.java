package mx.sipsi.negocio.delegate;

import mx.sipsi.entity.PacienteEntity;
import mx.sipsi.negocio.facade.ExpedienteFacade;

public class ExpedienteDelegate {

    private ExpedienteFacade expedienteFacade = new ExpedienteFacade();

    public PacienteEntity obtenerExpedienteCompleto(Long idPaciente) {
        return expedienteFacade.obtenerExpedienteCompleto(idPaciente);
    }
}