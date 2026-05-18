package mx.sipsi.negocio.facade;

import mx.sipsi.entity.ArchivoEntity;
import mx.sipsi.entity.PacienteEntity;
import mx.sipsi.negocio.integration.IArchivoNegocioIntegration;
import mx.sipsi.negocio.integration.ArchivoNegocioIntegrationImpl;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class ArchivoFacade {

    private IArchivoNegocioIntegration archivoNegocioIntegration;

    public ArchivoFacade() {
        this.archivoNegocioIntegration = new ArchivoNegocioIntegrationImpl();
    }

    public ArchivoEntity subirArchivo(InputStream inputStream, String nombreOriginal, PacienteEntity paciente) throws IOException {
        if (inputStream == null) {
            throw new IOException("No se recibió ningún archivo.");
        }

        if (nombreOriginal == null || nombreOriginal.trim().isEmpty()) {
            throw new IOException("El nombre del archivo no es válido.");
        }

        if (paciente == null || paciente.getId() <= 0) {
            throw new IOException("No se encontró el paciente asociado al archivo.");
        }

        return archivoNegocioIntegration.subirArchivo(inputStream, nombreOriginal, paciente);
    }

    public ArchivoEntity buscarPorId(Long idArchivo) throws IOException {
        if (idArchivo == null || idArchivo <= 0) {
            throw new IOException("No se encontró el archivo seleccionado.");
        }

        return archivoNegocioIntegration.buscarPorId(idArchivo);
    }

    public List<ArchivoEntity> listarPorPaciente(int idPaciente) throws IOException {
        if (idPaciente <= 0) {
            throw new IOException("No se encontró el paciente asociado al expediente.");
        }

        return archivoNegocioIntegration.listarPorPaciente(idPaciente);
    }

    public void eliminarArchivo(Long idArchivo) throws IOException {
        if (idArchivo == null || idArchivo <= 0) {
            throw new IOException("No se encontró el archivo seleccionado.");
        }

        archivoNegocioIntegration.eliminarArchivo(idArchivo);
    }
}