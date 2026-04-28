package mx.sipsi.negocio.integration;

import mx.sipsi.entity.PacienteEntity;
import mx.sipsi.persistence.dao.PacienteDAO;
import mx.sipsi.persistence.integration.IPacientePersistenciaIntegration;
import java.util.List;

public class PacienteNegocioIntegrationImpl implements IPacienteNegocioIntegration {

    private IPacientePersistenciaIntegration persistencia = new PacienteDAO();

    @Override
    public void llamarIntegracion(PacienteEntity paciente) throws Exception {


        if (paciente.getTelefono() == null || paciente.getTelefono().trim().isEmpty()) {
            throw new Exception("El teléfono es obligatorio y no puede estar vacío.");
        }


        if (persistencia.existePaciente(paciente.getCorreo())) {
            throw new Exception("El paciente con este correo ya está registrado.");
        }


        if (persistencia.existePaciente(paciente)) {
            throw new Exception(": Ya existe un paciente registrado con el mismo nombre, fecha de nacimiento y teléfono.");
        }

        persistencia.insertar(paciente);
    }

    @Override
    public List<PacienteEntity> buscarTodosActivos() {
        return persistencia.buscarTodosActivos();
    }

    @Override
    public List<PacienteEntity> buscarPorNombreActivos(String nombre) {
        return persistencia.buscarPorNombreActivos(nombre);
    }

    @Override
    public void archivarPaciente(int idPaciente) {
        PacienteEntity paciente = persistencia.consultarPorId(idPaciente);

        if (paciente == null) {
            throw new IllegalArgumentException("No se encontró el paciente");
        }

        if (!paciente.isActivo()) {
            throw new IllegalArgumentException("El paciente ya se encuentra archivado");
        }

        persistencia.archivarPaciente(idPaciente);
    }

    @Override
    public void recuperarPaciente(int idPaciente) {
        PacienteEntity paciente = persistencia.consultarPorId(idPaciente);

        if (paciente == null) {
            throw new IllegalArgumentException("No se encontró el paciente");
        }

        if (paciente.isActivo()) {
            throw new IllegalArgumentException("El paciente ya se encuentra activo");
        }

        persistencia.recuperarPaciente(idPaciente);
    }

    @Override
    public void eliminarDefinitivamente(int idPaciente) {
        PacienteEntity paciente = persistencia.consultarPorId(idPaciente);

        if (paciente == null) {
            throw new IllegalArgumentException("No se encontró el paciente");
        }

        if (paciente.isActivo()) {
            throw new IllegalArgumentException("Solo se pueden eliminar definitivamente pacientes archivados");
        }

        persistencia.eliminarDefinitivamente(idPaciente);
    }

    @Override
    public List<PacienteEntity> listarPacientesArchivados() {
        return persistencia.listarPacientesArchivados();
    }

    @Override
    public PacienteEntity obtenerExpedienteCompleto(Long idPaciente) {
        return persistencia.obtenerExpedienteCompleto(idPaciente);
    }
}