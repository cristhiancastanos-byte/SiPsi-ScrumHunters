package mx.sipsi.persistence.dao;

import mx.sipsi.entity.PacienteEntity;
import mx.sipsi.persistence.integration.IPacientePersistenciaIntegration;
import mx.sipsi.persistence.persistence.PacientePersistence;
import java.util.List;

public class PacienteDAO implements IPacientePersistenciaIntegration {

    private PacientePersistence persistence = new PacientePersistence();

    @Override
    public void insertar(PacienteEntity paciente) {
        try {
            persistence.executeTransaction(paciente);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean existePaciente(String correo) {
        return persistence.checkExists(correo);
    }

    @Override
    public List<PacienteEntity> buscarTodosActivos() {
        return persistence.executeSelectTodosActivos();
    }

    @Override
    public List<PacienteEntity> buscarPorNombreActivos(String nombre) {
        return persistence.executeSelectByNombreActivos(nombre);
    }
}