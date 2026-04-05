package mx.sipsi.negocio.integration;

import mx.sipsi.entity.PacienteEntity;
import mx.sipsi.persistence.dao.PacienteDAO;
import mx.sipsi.persistence.integration.IPacientePersistenciaIntegration;

public class PacienteNegocioIntegrationImpl implements IPacienteNegocioIntegration {

    private IPacientePersistenciaIntegration persistencia = new PacienteDAO();

    @Override
    public void llamarIntegracion(PacienteEntity paciente) throws Exception {

        if (persistencia.existePaciente(paciente.getCorreo())) {
            throw new Exception("Validación de Negocio: El paciente con este correo ya está registrado.");
        }

        persistencia.insertar(paciente);
    }
}