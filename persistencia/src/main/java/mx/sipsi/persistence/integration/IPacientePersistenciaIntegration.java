package mx.sipsi.persistence.integration;

import java.util.Date;
import java.util.List;
import mx.sipsi.entity.PacienteEntity;

public interface IPacientePersistenciaIntegration {

    void insertar(PacienteEntity paciente);

    boolean existePaciente(String correo);

    List<PacienteEntity> buscarTodosActivos();

    List<PacienteEntity> buscarPorNombreActivos(String nombre);

    PacienteEntity consultarPorId(int id);

    PacienteEntity buscarDuplicado(String nombre, Date fechaNac, int idActual);

    void actualizar(PacienteEntity paciente);

    boolean darDeBajaLogica(int idPaciente);

    void archivarPaciente(int idPaciente);

    void recuperarPaciente(int idPaciente);

    void eliminarDefinitivamente(int idPaciente);

    List<PacienteEntity> listarPacientesArchivados();
}