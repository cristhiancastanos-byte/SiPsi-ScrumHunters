package mx.sipsi.persistence.integration;

import mx.sipsi.entity.PacienteEntity;
import java.util.List;

public interface IPacientePersistenciaIntegration {
    void insertar(PacienteEntity paciente);
    boolean existePaciente(String correo);

    List<PacienteEntity> buscarTodosActivos();
    List<PacienteEntity> buscarPorNombreActivos(String nombre);
}