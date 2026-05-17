package mx.sipsi.negocio.delegate;

import mx.sipsi.entity.ArchivoEntity;
import mx.sipsi.entity.PacienteEntity;
import mx.sipsi.negocio.facade.ArchivoFacade;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class ArchivoDelegate {

    private ArchivoFacade archivoFacade;

    public ArchivoDelegate() {
        this.archivoFacade = new ArchivoFacade();
    }

    public ArchivoEntity subirArchivo(InputStream inputStream, String nombreOriginal, PacienteEntity paciente) throws IOException {
        return archivoFacade.subirArchivo(inputStream, nombreOriginal, paciente);
    }

    public ArchivoEntity buscarPorId(Long idArchivo) throws IOException {
        return archivoFacade.buscarPorId(idArchivo);
    }

    public List<ArchivoEntity> listarPorPaciente(int idPaciente) throws IOException {
        return archivoFacade.listarPorPaciente(idPaciente);
    }
}
