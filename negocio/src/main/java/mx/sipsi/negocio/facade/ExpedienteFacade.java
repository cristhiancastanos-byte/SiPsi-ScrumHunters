package mx.sipsi.negocio.facade;

import mx.sipsi.entity.PacienteEntity;
import mx.sipsi.negocio.integration.IPacienteNegocioIntegration;
import mx.sipsi.negocio.integration.PacienteNegocioIntegrationImpl;

public class ExpedienteFacade {

    private IPacienteNegocioIntegration pacienteNegocioIntegration = new PacienteNegocioIntegrationImpl();

    public PacienteEntity obtenerExpedienteCompleto(Long idPaciente) {

        if (idPaciente == null) {
            throw new IllegalArgumentException("El id del paciente es obligatorio");
        }

        return pacienteNegocioIntegration.obtenerExpedienteCompleto(idPaciente);
    }
}