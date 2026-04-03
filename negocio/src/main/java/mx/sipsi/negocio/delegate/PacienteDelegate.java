package mx.sipsi.negocio.delegate;


import mx.sipsi.entity.PacienteEntity;
import mx.sipsi.negocio.facade.PacienteFacade;

public class PacienteDelegate {

    private PacienteFacade facade = new PacienteFacade();

    public void registrarPaciente(PacienteEntity paciente) throws Exception {
        facade.procesarAlta(paciente);
    }
}