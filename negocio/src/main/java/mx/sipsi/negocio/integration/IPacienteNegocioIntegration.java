package mx.sipsi.negocio.integration;

import mx.sipsi.entity.PacienteEntity;
import java.util.List;

public interface IPacienteNegocioIntegration {
    void llamarIntegracion(PacienteEntity paciente) throws Exception;

    List<PacienteEntity> buscarTodosActivos();
    List<PacienteEntity> buscarPorNombreActivos(String nombre);
}