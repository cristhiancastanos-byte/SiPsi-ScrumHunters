package mx.sipsi.persistence.integration;

import mx.sipsi.entity.ArchivoEntity;
import java.util.List;

public interface IArchivoPersistenceIntegration {

    ArchivoEntity guardarRutaArchivo(ArchivoEntity archivo) throws Exception;

    ArchivoEntity buscarPorId(Long idArchivo) throws Exception;

    List<ArchivoEntity> listarPorPaciente(int idPaciente) throws Exception;
}
