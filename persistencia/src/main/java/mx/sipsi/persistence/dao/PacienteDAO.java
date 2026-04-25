package mx.sipsi.persistence.dao;

import java.util.Date;
import java.util.List;
import mx.sipsi.entity.PacienteEntity;
import mx.sipsi.persistence.integration.IPacientePersistenciaIntegration;
import mx.sipsi.persistence.persistence.PacientePersistence;

public class PacienteDAO implements IPacientePersistenciaIntegration {

    private final PacientePersistence persistence = new PacientePersistence();


    private void validarFormatos(PacienteEntity paciente) {
        String regexTelefono = "^\\+?[0-9\\-\\s]{10,15}$";
        String regexCorreo = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

        if (paciente.getTelefono() == null || !paciente.getTelefono().matches(regexTelefono)) {
            throw new IllegalArgumentException("El numero de telefono ingresado no tiene un formato valido.");
        }

        if (paciente.getCorreo() != null && !paciente.getCorreo().trim().isEmpty()) {
            if (!paciente.getCorreo().matches(regexCorreo)) {
                throw new IllegalArgumentException("El correo ingresado no tiene un formato valido.");
            }
        }
    }

    @Override
    public void insertar(PacienteEntity paciente) {
        try {
            validarFormatos(paciente);
            persistence.executeTransaction(paciente);
        } catch (Exception e) {
            throw new RuntimeException("Error al insertar paciente: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean existePaciente(String correo) {
        return persistence.checkExists(correo);
    }

    @Override
    public boolean existePaciente(PacienteEntity paciente) {
        return checkDuplicate(paciente.getNombre(), paciente.getFechaNac(), paciente.getTelefono());
    }

    @Override
    public boolean checkDuplicate(String nombre, Date fechaNac, String telefono) {
        return persistence.checkDuplicate(nombre, fechaNac, telefono);
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
    public PacienteEntity buscarDuplicado(String nombre, Date fechaNac, String telefono, int idActual) {
        try {
            return persistence.executeFindDuplicado(nombre, fechaNac, telefono, idActual);
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar paciente duplicado", e);
        }
    }

    @Override
    public void actualizar(PacienteEntity paciente) {
        try {
            validarFormatos(paciente);
            persistence.executeUpdate(paciente);
        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar paciente: " + e.getMessage(), e);
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
}