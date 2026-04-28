package mx.sipsi.helper;

import jakarta.enterprise.context.RequestScoped;
import mx.sipsi.entity.PacienteEntity;
import java.io.Serializable;

@RequestScoped
public class PacienteHelper implements Serializable {
    private static final long serialVersionUID = 1L;

    public boolean validarDatos(PacienteEntity paciente) {
        if (paciente == null) return false;

        return (paciente.getNombre() != null && !paciente.getNombre().trim().isEmpty())
                && (paciente.getFechaNac() != null)
                && (paciente.getGenero() != null && !paciente.getGenero().trim().isEmpty())
                && (paciente.getTelefono() != null && !paciente.getTelefono().trim().isEmpty());
    }
}