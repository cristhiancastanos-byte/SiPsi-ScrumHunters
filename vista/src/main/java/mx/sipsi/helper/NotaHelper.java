package mx.sipsi.helper;

import jakarta.enterprise.context.RequestScoped;
import java.io.Serializable;

@RequestScoped
public class NotaHelper implements Serializable {

    private static final long serialVersionUID = 1L;

    public boolean validarContenidoNota(String contenido) {
        return contenido != null && !contenido.trim().isEmpty();
    }
}