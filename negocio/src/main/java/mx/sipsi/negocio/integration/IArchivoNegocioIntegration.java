package mx.sipsi.negocio.integration;

import mx.sipsi.entity.ArchivoEntity;
import mx.sipsi.entity.PacienteEntity;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface IArchivoNegocioIntegration {

    ArchivoEntity subirArchivo(InputStream inputStream, String nombreOriginal, PacienteEntity paciente) throws IOException;

    ArchivoEntity buscarPorId(Long idArchivo) throws IOException;

    List<ArchivoEntity> listarPorPaciente(int idPaciente) throws IOException;

    void eliminarArchivo(Long idArchivo) throws IOException;
}