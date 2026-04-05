package mx.sipsi.persistence.integration;

import mx.sipsi.entity.PacienteEntity;

public interface IPacientePersistenciaIntegration {
    void insertar(PacienteEntity paciente);
    boolean existePaciente(String correo);
}