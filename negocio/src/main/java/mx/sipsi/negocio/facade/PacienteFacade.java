package mx.sipsi.negocio.facade;

import java.util.List;
import mx.sipsi.entity.PacienteEntity;
import mx.sipsi.persistence.dao.PacienteDAO;

public class PacienteFacade {

    private final PacienteDAO pacienteDAO = new PacienteDAO();

    public void procesarAlta(PacienteEntity paciente) {

        if (paciente == null
                || paciente.getNombre() == null || paciente.getNombre().trim().isEmpty()
                || paciente.getFechaNac() == null
                || paciente.getGenero() == null || paciente.getGenero().trim().isEmpty()
                || paciente.getTelefono() == null || paciente.getTelefono().trim().isEmpty()) {
            throw new IllegalArgumentException("Complete todos los campos obligatorios");
        }

       PacienteEntity duplicado = pacienteDAO.buscarDuplicado(
                paciente.getNombre(),
                paciente.getFechaNac(),
                paciente.getTelefono(),
                0
        );

        if (duplicado != null) {
            throw new IllegalArgumentException("Paciente ya existe");
        }

        pacienteDAO.insertar(paciente);
    }

    public List<PacienteEntity> buscarTodosActivos() {
        return pacienteDAO.buscarTodosActivos();
    }

    public List<PacienteEntity> buscarPorNombreActivos(String nombre) {
        return pacienteDAO.buscarPorNombreActivos(nombre);
    }

    public PacienteEntity procesarConsultaPorId(int id) {
        return pacienteDAO.consultarPorId(id);
    }

    public void procesarActualizacion(PacienteEntity paciente) {

        if (paciente == null
                || paciente.getNombre() == null || paciente.getNombre().trim().isEmpty()
                || paciente.getFechaNac() == null
                || paciente.getGenero() == null || paciente.getGenero().trim().isEmpty()
                || paciente.getTelefono() == null || paciente.getTelefono().trim().isEmpty()) {
            throw new IllegalArgumentException("Complete todos los campos obligatorios");
        }

        PacienteEntity duplicado = pacienteDAO.buscarDuplicado(
                paciente.getNombre(),
                paciente.getFechaNac(),
                paciente.getTelefono(),
                paciente.getId()
        );

        if (duplicado != null) {
            throw new IllegalArgumentException("Paciente ya existe");
        }

        pacienteDAO.actualizar(paciente);
    }

    public void procesarBajaLogica(int idPaciente) {
        if (idPaciente <= 0) {
            throw new IllegalArgumentException("Paciente invalido");
        }

        boolean eliminado = pacienteDAO.darDeBajaLogica(idPaciente);

        if (!eliminado) {
            throw new IllegalArgumentException("No se encontró el paciente");
        }
    }
}