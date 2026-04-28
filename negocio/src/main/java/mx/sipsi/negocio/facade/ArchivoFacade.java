package mx.sipsi.negocio.facade;

import mx.sipsi.entity.PacienteEntity;
import mx.sipsi.negocio.integration.IArchivoNegocioIntegration;
import mx.sipsi.negocio.integration.ArchivoNegocioIntegrationImpl;

import java.io.IOException;
import java.io.InputStream;

public class ArchivoFacade {

    private IArchivoNegocioIntegration archivoNegocioIntegration;

    public ArchivoFacade() {
        this.archivoNegocioIntegration = new ArchivoNegocioIntegrationImpl();
    }

    public void subirArchivo(InputStream inputStream, String nombreOriginal, PacienteEntity paciente) throws IOException {
        archivoNegocioIntegration.subirArchivo(inputStream, nombreOriginal, paciente);
    }
}