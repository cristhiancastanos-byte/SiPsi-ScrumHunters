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
        return archivoNegocioIntegration.subirArchivo(inputStream, nombreOriginal, paciente);
    }

    public ArchivoEntity buscarPorId(Long idArchivo) throws IOException {
        return archivoNegocioIntegration.buscarPorId(idArchivo);
    }

    public List<ArchivoEntity> listarPorPaciente(int idPaciente) throws IOException {
        return archivoNegocioIntegration.listarPorPaciente(idPaciente);
    }
}
