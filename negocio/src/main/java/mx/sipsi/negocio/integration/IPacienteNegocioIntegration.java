package mx.sipsi.negocio.integration;

import mx.sipsi.entity.PacienteEntity;

public interface IPacienteNegocioIntegration {
    void llamarIntegracion(PacienteEntity paciente) throws Exception;
}