package mx.sipsi.persistence.dao;

import java.util.Date;
import java.util.List;
import mx.sipsi.entity.PacienteEntity;
import mx.sipsi.persistence.integration.IPacientePersistenciaIntegration;
import mx.sipsi.persistence.persistence.PacientePersistence;

public class PacienteDAO implements IPacientePersistenciaIntegration {

    private final PacientePersistence persistence = new PacientePersistence();

    @Override
    public void insertar(PacienteEntity paciente) {
        try {
            persistence.executeTransaction(paciente);
        } catch (Exception e) {
            throw new RuntimeException("Error al insertar paciente", e);
        }
    }

    @Override
    public boolean existePaciente(String correo) {
        return persistence.checkExists(correo);
    }

    @Override
    public List<PacienteEntity> buscarTodosActivos() {
        try {
            return persistence.executeFindAllActivos();
        } catch (Exception e) {
            throw new RuntimeException("Error al consultar pacientes activos", e);
        }
    }

    @Override
    public List<PacienteEntity> buscarPorNombreActivos(String nombre) {
        try {
            return persistence.executeFindByNombreActivos(nombre);
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar pacientes por nombre", e);
        }
    }

    @Override
    public PacienteEntity consultarPorId(int id) {
        try {
            return persistence.executeSelectById(id);
        } catch (Exception e) {
            throw new RuntimeException("Error al consultar paciente por id", e);
        }
    }

    @Override
    public PacienteEntity buscarDuplicado(String nombre, Date fechaNac, int idActual) {
        try {
            return persistence.executeFindDuplicado(nombre, fechaNac, idActual);
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar paciente duplicado", e);
        }
    }

    @Override
    public void actualizar(PacienteEntity paciente) {
        try {
            persistence.executeUpdate(paciente);
        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar paciente", e);
        }
    }

    @Override
    public boolean darDeBajaLogica(int idPaciente) {
        try {
            return persistence.executeBajaLogica(idPaciente);
        } catch (Exception e) {
            throw new RuntimeException("Error al dar de baja lógica al paciente", e);
        }
    }

    @Override
    public void archivarPaciente(int idPaciente) {
        try {
            persistence.executeArchivarPaciente(idPaciente);
        } catch (Exception e) {
            throw new RuntimeException("Error al archivar paciente", e);
        }
    }

    @Override
    public void recuperarPaciente(int idPaciente) {
        try {
            persistence.executeRecuperarPaciente(idPaciente);
        } catch (Exception e) {
            throw new RuntimeException("Error al recuperar paciente", e);
        }
    }

    @Override
    public void eliminarDefinitivamente(int idPaciente) {
        try {
            persistence.executeDeletePaciente(idPaciente);
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar definitivamente al paciente", e);
        }
    }

    @Override
    public List<PacienteEntity> listarPacientesArchivados() {
        try {
            return persistence.executeFindAllArchivados();
        } catch (Exception e) {
            throw new RuntimeException("Error al consultar pacientes archivados", e);
        }
    }
}