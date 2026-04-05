package mx.sipsi.negocio.facade;

import mx.sipsi.entity.PacienteEntity;
import mx.sipsi.negocio.integration.IPacienteNegocioIntegration;
import mx.sipsi.negocio.integration.PacienteNegocioIntegrationImpl;

public class PacienteFacade {

    private IPacienteNegocioIntegration integration = new PacienteNegocioIntegrationImpl();

    public void procesarAlta(PacienteEntity paciente) throws Exception {
        integration.llamarIntegracion(paciente);
    }
}