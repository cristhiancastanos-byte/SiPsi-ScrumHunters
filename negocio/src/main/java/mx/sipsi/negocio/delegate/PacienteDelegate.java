package mx.sipsi.negocio.delegate;

import mx.sipsi.entity.PacienteEntity;
import mx.sipsi.negocio.facade.PacienteFacade;
import java.util.List;

public class PacienteDelegate {

    private PacienteFacade facade = new PacienteFacade();

    public void registrarPaciente(PacienteEntity paciente) throws Exception {

        facade.procesarAlta(paciente);
    }

    public List<PacienteEntity> buscarTodosActivos() {
        return facade.buscarTodosActivos();
    }

    public List<PacienteEntity> buscarPorNombreActivos(String nombre) {
        return facade.buscarPorNombreActivos(nombre);
    }

    public PacienteEntity consultarPorId(int idPaciente) {
        return facade.procesarConsultaPorId(idPaciente);
    }
}