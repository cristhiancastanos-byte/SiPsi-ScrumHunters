package mx.sipsi.negocio.delegate;

import mx.sipsi.entity.PacienteEntity;
import mx.sipsi.negocio.facade.ArchivoFacade;

import java.io.IOException;
import java.io.InputStream;

public class ArchivoDelegate {

    private ArchivoFacade archivoFacade;

    public ArchivoDelegate() {
        this.archivoFacade = new ArchivoFacade();
    }

    public void subirArchivo(InputStream inputStream, String nombreOriginal, PacienteEntity paciente) throws IOException {
        archivoFacade.subirArchivo(inputStream, nombreOriginal, paciente);
    }
}