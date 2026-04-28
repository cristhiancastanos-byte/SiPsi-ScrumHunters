package mx.sipsi.persistence.dao;

import mx.sipsi.entity.ArchivoEntity;
import mx.sipsi.persistence.integration.IArchivoPersistenceIntegration;
import mx.sipsi.persistence.persistence.ArchivoPersistence;

import java.util.List;

public class ArchivoDAO implements IArchivoPersistenceIntegration {

    private ArchivoPersistence archivoPersistence;

    public ArchivoDAO() {
        this.archivoPersistence = new ArchivoPersistence();
    }

    @Override
    public ArchivoEntity guardarRutaArchivo(ArchivoEntity archivo) throws Exception {
        return archivoPersistence.guardarRutaArchivo(archivo);
    }

    @Override
    public ArchivoEntity buscarPorId(Long idArchivo) throws Exception {
        return archivoPersistence.buscarPorId(idArchivo);
    }

    @Override
    public List<ArchivoEntity> listarPorPaciente(int idPaciente) throws Exception {
        return archivoPersistence.listarPorPaciente(idPaciente);
    }

    @Override
    public void eliminarArchivo(Long idArchivo) throws Exception {
        archivoPersistence.eliminarArchivo(idArchivo);
    }
}