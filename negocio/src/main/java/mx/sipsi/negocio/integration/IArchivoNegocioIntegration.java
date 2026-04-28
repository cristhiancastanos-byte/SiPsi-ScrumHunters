package mx.sipsi.negocio.integration;


import mx.sipsi.entity.PacienteEntity;

import java.io.IOException;
import java.io.InputStream;

public interface IArchivoNegocioIntegration {

    void subirArchivo(InputStream inputStream, String nombreOriginal, PacienteEntity paciente) throws IOException;
}